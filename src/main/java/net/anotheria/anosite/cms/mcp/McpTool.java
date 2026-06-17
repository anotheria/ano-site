package net.anotheria.anosite.cms.mcp;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * A single callable tool exposed via the MCP endpoint.
 */
public interface McpTool {

    String name();

    String description();

    JSONObject inputSchema() throws JSONException;

    String execute(JSONObject arguments) throws Exception;
}
