package net.anotheria.anosite.cms.mcp;

import net.anotheria.anosite.cms.mcp.tools.LocalizationTools;
import net.anotheria.anosite.cms.mcp.tools.TextResourceTools;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers all MCP tools and dispatches tool/list and tools/call requests.
 */
public class McpToolRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(McpToolRegistry.class);

    private final Map<String, McpTool> tools = new LinkedHashMap<>();

    public McpToolRegistry() {
        register(new LocalizationTools.ListBundles());
        register(new LocalizationTools.GetBundle());
        register(new LocalizationTools.SetBundleKey());
        register(new TextResourceTools.ListTextResources());
        register(new TextResourceTools.GetTextResource());
        register(new TextResourceTools.UpdateTextResource());
    }

    public void register(McpTool tool) {
        tools.put(tool.name(), tool);
    }

    public void registerBundle(List<McpTool> bundle) {
        bundle.forEach(t -> tools.put(t.name(), t));
    }

    public JSONObject initialize() throws JSONException {
        JSONObject caps = new JSONObject();
        caps.put("tools", new JSONObject());
        JSONObject info = new JSONObject();
        info.put("name", "AnoSite CMS");
        info.put("version", "1.0.0");
        JSONObject result = new JSONObject();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", caps);
        result.put("serverInfo", info);
        return result;
    }

    public JSONObject toolsList() throws JSONException {
        JSONArray list = new JSONArray();
        for (McpTool tool : tools.values()) {
            JSONObject entry = new JSONObject();
            entry.put("name", tool.name());
            entry.put("description", tool.description());
            entry.put("inputSchema", tool.inputSchema());
            list.put(entry);
        }
        JSONObject result = new JSONObject();
        result.put("tools", list);
        return result;
    }

    public JSONObject toolCall(JSONObject params) throws JSONException {
        String name = params.getString("name");
        JSONObject arguments = params.optJSONObject("arguments");
        if (arguments == null) arguments = new JSONObject();

        McpTool tool = tools.get(name);
        if (tool == null)
            return errorContent("Unknown tool: " + name);

        try {
            String text = tool.execute(arguments);
            JSONArray content = new JSONArray();
            content.put(new JSONObject().put("type", "text").put("text", text));
            return new JSONObject().put("content", content);
        } catch (Exception e) {
            LOG.error("Tool {} failed", name, e);
            return errorContent("Tool execution failed: " + e.getMessage());
        }
    }

    private JSONObject errorContent(String message) throws JSONException {
        JSONArray content = new JSONArray();
        content.put(new JSONObject().put("type", "text").put("text", message));
        return new JSONObject().put("content", content).put("isError", true);
    }
}
