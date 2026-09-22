package net.anotheria.anosite.cms.mcp;

import java.util.List;

/**
 * Tool registration entry point of the hand written MCP endpoint.
 *
 * <p>The endpoint itself is gone: serving MCP is done by the official sdk now, set up by {@link McpBootstrap},
 * and the deprecated http+sse transport this class implemented was replaced by stateless streamable http.
 * Only the two registration methods survive, forwarding to {@link McpToolRegistry}, so projects that call
 * {@code McpEndpoint.addBundle(...)} from their context listener keep compiling.
 *
 * @deprecated call {@link McpToolRegistry#addTool(McpTool)} / {@link McpToolRegistry#addBundle(List)} instead.
 *             This class will be removed.
 */
@Deprecated(forRemoval = true)
public final class McpEndpoint {

    private McpEndpoint() {
    }

    /**
     * @param tool tool to expose
     * @deprecated use {@link McpToolRegistry#addTool(McpTool)}.
     */
    @Deprecated(forRemoval = true)
    public static void addTool(McpTool tool) {
        McpToolRegistry.addTool(tool);
    }

    /**
     * @param bundle tools to expose
     * @deprecated use {@link McpToolRegistry#addBundle(List)}.
     */
    @Deprecated(forRemoval = true)
    public static void addBundle(List<McpTool> bundle) {
        McpToolRegistry.addBundle(bundle);
    }
}
