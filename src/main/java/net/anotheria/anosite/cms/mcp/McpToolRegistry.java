package net.anotheria.anosite.cms.mcp;

import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import net.anotheria.anosite.cms.mcp.tools.LocalizationTools;
import net.anotheria.anosite.cms.mcp.tools.TextResourceTools;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the tools the MCP server exposes.
 *
 * <p>The shared registry is filled at startup: this class contributes the cms wide tools itself, projects add
 * their own with {@link #addTool(McpTool)} / {@link #addBundle(List)} from their context listener.
 * {@link McpBootstrap} reads it once when it builds the server, so registration has to happen before the
 * bootstrap listener runs — see its class comment for the web.xml ordering that guarantees that.
 *
 * <p>Dispatching tools/list and tools/call is no longer done here; that is the sdk's job since the endpoint
 * moved to the official MCP server implementation.
 */
public class McpToolRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(McpToolRegistry.class);

    /**
     * Registry the bootstrap builds its server from.
     */
    private static final McpToolRegistry SHARED = new McpToolRegistry();

    private final Map<String, McpTool> tools = new LinkedHashMap<>();

    public McpToolRegistry() {
        register(new LocalizationTools.ListBundles());
        register(new LocalizationTools.GetBundle());
        register(new LocalizationTools.SetBundleKey());
        register(new LocalizationTools.SetBundleKeys());
        register(new TextResourceTools.ListTextResources());
        register(new TextResourceTools.GetTextResource());
        register(new TextResourceTools.UpdateTextResource());
    }

    /**
     * The registry the MCP server is built from.
     *
     * @return shared registry
     */
    public static McpToolRegistry shared() {
        return SHARED;
    }

    /**
     * Adds one tool to the shared registry.
     *
     * @param tool tool to expose
     */
    public static void addTool(McpTool tool) {
        SHARED.register(tool);
    }

    /**
     * Adds a group of tools to the shared registry, typically one generated {@code *McpTools.all()}.
     *
     * @param bundle tools to expose
     */
    public static void addBundle(List<McpTool> bundle) {
        SHARED.registerBundle(bundle);
    }

    public void register(McpTool tool) {
        McpTool previous = tools.put(tool.name(), tool);
        if (previous != null)
            LOG.warn("Tool {} registered twice, {} replaces {}", tool.name(),
                    tool.getClass().getName(), previous.getClass().getName());
    }

    public void registerBundle(List<McpTool> bundle) {
        bundle.forEach(this::register);
    }

    /**
     * Registered tools, in registration order.
     *
     * @return registered tools
     */
    public Collection<McpTool> tools() {
        return tools.values();
    }

    /**
     * Builds the sdk specifications for all registered tools.
     *
     * <p>Input schemas are read here, once, at server build time. Some of them describe the project's data —
     * {@code LocalizationTools} for instance lists the supported languages of the running project — so the
     * cms tiers have to be configured by the time this runs.
     *
     * @return specifications to hand to the server builder
     * @throws JSONException if a tool's input schema can't be read
     */
    public List<McpStatelessServerFeatures.SyncToolSpecification> specifications() throws JSONException {
        List<McpStatelessServerFeatures.SyncToolSpecification> specifications = new ArrayList<>(tools.size());
        for (McpTool tool : tools.values())
            specifications.add(McpToolAdapter.toSpecification(tool));

        return specifications;
    }
}
