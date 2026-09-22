package net.anotheria.anosite.cms.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.ContextManager;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the MCP endpoint is only reachable with a configured api key, and that accepted requests carry the
 * mcp author on the call context.
 */
public class McpApiKeyFilterTest {

    private static final String VALID_KEY = "s3cret-key";

    private McpServerConfig config;
    private RecordingChain chain;
    private McpApiKeyFilter filter;
    private StringWriter responseBody;
    private final Map<String, Object> responseState = new HashMap<>();

    @BeforeClass
    public static void setUpCallContext() {
        ContextManager.setFactory(TestCallContext::new);
    }

    private static final class TestCallContext extends CallContext {
        @Override
        public String getDefaultLanguage() {
            return "EN";
        }

        @Override
        public List<String> getSupportedLanguages() {
            return List.of("EN");
        }
    }

    /**
     * Records whether the request made it past the filter, and what the call context looked like at that moment.
     */
    private static final class RecordingChain implements FilterChain {
        private boolean invoked;
        private String authorDuringCall;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            invoked = true;
            authorDuringCall = ContextManager.getCallContext().getCurrentAuthor();
        }
    }

    @Before
    public void setUp() {
        config = McpServerConfig.getInstance();
        config.setEnabled(true);
        config.setApiKeys(new String[]{VALID_KEY});
        config.setAuthor("mcp");

        chain = new RecordingChain();
        //the filter reads its config per request; hand it the one this test set up
        filter = new McpApiKeyFilter() {
            @Override
            protected McpServerConfig config() {
                return config;
            }
        };
        responseBody = new StringWriter();
        responseState.clear();
    }

    @After
    public void tearDown() {
        //the config is a singleton, leave it the way an unconfigured project would see it
        config.setEnabled(false);
        config.setApiKeys(new String[0]);
        ContextManager.getCallContext().setCurrentAuthor(null);
    }

    private HttpServletRequest request(String headerKey, String parameterKey) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getHeader" -> McpApiKeyFilter.API_KEY_HEADER.equals(args[0]) ? headerKey : null;
                    case "getParameter" -> McpApiKeyFilter.API_KEY_PARAMETER.equals(args[0]) ? parameterKey : null;
                    case "getRequestURI" -> "/mcp";
                    case "getRemoteAddr" -> "127.0.0.1";
                    default -> null;
                });
    }

    private HttpServletResponse response() {
        return (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "setStatus" -> responseState.put("status", args[0]);
                    case "setContentType" -> responseState.put("contentType", args[0]);
                    case "getWriter" -> new PrintWriter(responseBody);
                    default -> null;
                });
    }

    private int status() {
        return (Integer) responseState.get("status");
    }

    @Test
    public void requestsWithoutAnApiKeyAreRejected() throws Exception {
        filter.doFilter(request(null, null), response(), chain);

        assertFalse("an unauthenticated request must not reach the tools", chain.invoked);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, status());
    }

    @Test
    public void requestsWithAWrongApiKeyAreRejected() throws Exception {
        filter.doFilter(request("not-the-key", null), response(), chain);

        assertFalse(chain.invoked);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, status());
    }

    @Test
    public void aKeyThatIsOnlyAPrefixIsRejected() throws Exception {
        filter.doFilter(request(VALID_KEY.substring(0, 4), null), response(), chain);

        assertFalse(chain.invoked);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, status());
    }

    @Test
    public void theApiKeyIsAcceptedInTheHeader() throws Exception {
        filter.doFilter(request(VALID_KEY, null), response(), chain);

        assertTrue(chain.invoked);
    }

    @Test
    public void theApiKeyIsAcceptedAsARequestParameter() throws Exception {
        filter.doFilter(request(null, VALID_KEY), response(), chain);

        assertTrue("clients that can't set headers fall back to the parameter", chain.invoked);
    }

    @Test
    public void aDisabledServerAnswersNotFound() throws Exception {
        config.setEnabled(false);

        filter.doFilter(request(VALID_KEY, null), response(), chain);

        assertFalse(chain.invoked);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, status());
    }

    @Test
    public void acceptedRequestsRunAsTheConfiguredAuthor() throws Exception {
        config.setAuthor("houseid-mcp");

        filter.doFilter(request(VALID_KEY, null), response(), chain);

        assertEquals("houseid-mcp", chain.authorDuringCall);
        assertNull("the author must not leak to the next request on this thread",
                ContextManager.getCallContext().getCurrentAuthor());
    }

    @Test
    public void refusalsAreReadableJsonRpcErrors() throws Exception {
        filter.doFilter(request(null, null), response(), chain);

        assertEquals("application/json", responseState.get("contentType"));
        JSONObject body = new JSONObject(responseBody.toString());
        assertEquals("2.0", body.getString("jsonrpc"));
        assertTrue(body.isNull("id"));
        assertEquals(-32001, body.getJSONObject("error").getInt("code"));
        assertEquals("Invalid or missing api key", body.getJSONObject("error").getString("message"));
    }
}
