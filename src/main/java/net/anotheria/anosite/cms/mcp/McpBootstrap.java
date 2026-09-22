package net.anotheria.anosite.cms.mcp;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.server.McpStatelessSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStatelessServerTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;

/**
 * Brings up the MCP server and registers its transport servlet.
 *
 * <p>The transport is streamable http in its stateless flavour: one endpoint, a json response per posted
 * request, no sessions and no long lived connections. That is all the cms needs — its tools answer the call
 * they were made in and the server never pushes anything on its own — and it is what makes the endpoint safe
 * behind a load balancer, since no request depends on reaching the node an earlier one hit.
 *
 * <p>Register it in web.xml <b>after</b> the project's own context listener:
 * <pre>
 *   &lt;listener&gt;
 *       &lt;listener-class&gt;net.anotheria.anosite.cms.mcp.McpBootstrap&lt;/listener-class&gt;
 *   &lt;/listener&gt;
 * </pre>
 * Listeners run in declaration order and this one reads the tool registry and the tools' input schemas once,
 * at startup, so the project has to have set its call context factory, configured the tiers and registered its
 * tools by the time it runs.
 *
 * <p>The sdk is an optional dependency of ano-site. Projects that declare this listener have to pull
 * {@code io.modelcontextprotocol.sdk:mcp-core} and {@code mcp-json-jackson2} themselves.
 */
public class McpBootstrap implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(McpBootstrap.class);

    private static final String SERVLET_NAME = "mcp-transport-servlet";
    private static final String FILTER_NAME = "mcp-api-key-filter";

    private McpStatelessSyncServer server;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        McpServerConfig config = McpServerConfig.getInstance();
        if (!config.isEnabled()) {
            LOG.info("MCP server is disabled, no endpoint registered.");
            return;
        }

        if (config.getApiKeys().length == 0) {
            LOG.error("MCP server is enabled but no apiKeys are configured, refusing to register the endpoint.");
            return;
        }

        List<McpStatelessServerFeatures.SyncToolSpecification> tools;
        try {
            tools = McpToolRegistry.shared().specifications();
        } catch (Exception e) {
            LOG.error("Cannot build the MCP tool specifications, no endpoint registered.", e);
            return;
        }

        HttpServletStatelessServerTransport transport = HttpServletStatelessServerTransport.builder()
                .messageEndpoint(config.getServletPath())
                .maxRequestSize(config.getMaxRequestSizeBytes())
                .build();

        McpServer.StatelessSyncSpecification specification = McpServer.sync(transport)
                .serverInfo(config.getServerName(), config.getServerVersion())
                .capabilities(McpSchema.ServerCapabilities.builder().tools(false).build())
                //the sdk validates call arguments against the tool's input schema before the handler runs, so
                //a tool never sees arguments its schema rejects.
                .validateToolInputs(true)
                //run tool handlers on the request thread instead of a scheduler thread. The cms tools read the
                //thread local CallContext - for the current language and the author McpApiKeyFilter put there -
                //and that context only exists on the thread the request came in on.
                .immediateExecution(true)
                .tools(tools);

        if (!config.getInstructions().isEmpty())
            specification = specification.instructions(config.getInstructions());

        server = specification.build();

        //filter and servlet share one mapping on purpose: a filter that covered less than the whole endpoint
        //would leave part of it unauthenticated.
        String mapping = config.getServletMapping();

        FilterRegistration.Dynamic filter = event.getServletContext().addFilter(FILTER_NAME, new McpApiKeyFilter());
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, mapping);

        ServletRegistration.Dynamic servlet = event.getServletContext().addServlet(SERVLET_NAME, transport);
        servlet.addMapping(mapping);

        LOG.info("MCP server '{}' started with {} tools, endpoint {}",
                config.getServerName(), tools.size(), config.getServletPath());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (server != null) {
            server.closeGracefully();
            LOG.info("MCP server stopped.");
        }
    }
}
