package net.anotheria.anosite.cms.mcp;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.ContextManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Guards the MCP endpoint.
 *
 * <p>MCP clients are machines and have no CMS login, so instead of a cms session they present a shared api key.
 * Without this filter the endpoint would be open to everyone who can reach the cms host — its tools write to
 * localization bundles and text resources, so that is not acceptable.
 *
 * <p>Accepted requests run with the configured author on the {@link CallContext}, which is what the cms records
 * on the documents the tools change. The generated cms actions do the same with the logged in user.
 */
public class McpApiKeyFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(McpApiKeyFilter.class);

    /**
     * Header carrying the api key.
     */
    public static final String API_KEY_HEADER = "X-API-Key";

    /**
     * Fallback for clients that can't set headers.
     */
    public static final String API_KEY_PARAMETER = "apiKey";

    /**
     * JSON-RPC error code for an unauthenticated caller. Outside the spec's reserved range, the way the MCP
     * implementations report transport level refusals.
     */
    private static final int ERROR_CODE_UNAUTHORIZED = -32001;

    /**
     * Read per request so keys can be rotated without a redeploy. Overridable for tests, which run against
     * their own config rather than the configureme singleton.
     *
     * @return current configuration
     */
    protected McpServerConfig config() {
        return McpServerConfig.getInstance();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest request)
                || !(servletResponse instanceof HttpServletResponse response)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        McpServerConfig config = config();
        if (!config.isEnabled()) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "MCP server is disabled");
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null)
            apiKey = request.getParameter(API_KEY_PARAMETER);

        if (!config.isValidApiKey(apiKey)) {
            LOG.warn("Rejected MCP request to {} from {} - invalid or missing api key",
                    request.getRequestURI(), request.getRemoteAddr());
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing api key");
            return;
        }

        CallContext callContext = ContextManager.getCallContext();
        String previousAuthor = callContext.getCurrentAuthor();
        callContext.setCurrentAuthor(config.getAuthor());

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            //the call context is a thread local that nothing clears between requests, so leaving the mcp author
            //behind would hand it to whatever this tomcat thread serves next.
            callContext.setCurrentAuthor(previousAuthor);
        }
    }

    /**
     * Answers with a JSON-RPC error rather than an html error page — the caller is an MCP client, and a
     * refusal it can read beats one it has to guess at from the status code.
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JSONObject error = new JSONObject()
                    .put("code", ERROR_CODE_UNAUTHORIZED)
                    .put("message", message);
            response.getWriter().write(new JSONObject()
                    .put("jsonrpc", "2.0")
                    .put("id", JSONObject.NULL)
                    .put("error", error)
                    .toString());
        } catch (JSONException e) {
            //can't happen with constant keys, but the writer must not be left empty if it does
            LOG.error("Cannot render MCP error response", e);
            response.getWriter().write("{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":"
                    + ERROR_CODE_UNAUTHORIZED + ",\"message\":\"Unauthorized\"}}");
        }
    }
}
