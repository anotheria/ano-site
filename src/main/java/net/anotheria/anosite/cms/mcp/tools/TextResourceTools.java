package net.anotheria.anosite.cms.mcp.tools;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anosite.cms.mcp.McpTool;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

/**
 * MCP tools for reading and editing text resources.
 */
public final class TextResourceTools {

    private TextResourceTools() {}

    private static IASResourceDataService service() throws Exception {
        return MetaFactory.get(IASResourceDataService.class);
    }

    // -------------------------------------------------------------------------

    public static class ListTextResources implements McpTool {

        @Override public String name() { return "textresource_list"; }
        @Override public String description() { return "List all text resources with their id and name."; }

        @Override public JSONObject inputSchema() throws JSONException {
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject())
                    .put("required", new org.codehaus.jettison.json.JSONArray());
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            List<TextResource> resources = service().getTextResources();
            StringBuilder sb = new StringBuilder();
            for (TextResource r : resources)
                sb.append(r.getId()).append("  ").append(r.getName()).append("\n");
            return sb.toString().trim();
        }
    }

    // -------------------------------------------------------------------------

    public static class GetTextResource implements McpTool {

        @Override public String name() { return "textresource_get"; }
        @Override public String description() {
            return "Get a text resource by id. Returns its name and value for all languages.";
        }

        @Override public JSONObject inputSchema() throws JSONException {
            JSONObject props = new JSONObject();
            props.put("id", new JSONObject().put("type", "string").put("description", "TextResource id"));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray().put("id"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String id = arguments.getString("id");
            TextResource r = service().getTextResource(id);
            return "id:   " + r.getId()      + "\n" +
                   "name: " + r.getName()    + "\n" +
                   "EN:   " + r.getValueEN() + "\n" +
                   "DE:   " + r.getValueDE();
        }
    }

    // -------------------------------------------------------------------------

    public static class UpdateTextResource implements McpTool {

        @Override public String name() { return "textresource_update"; }
        @Override public String description() {
            return "Update the value of a text resource for a given language (EN or DE).";
        }

        @Override public JSONObject inputSchema() throws JSONException {
            JSONObject props = new JSONObject();
            props.put("id",       new JSONObject().put("type", "string").put("description", "TextResource id"));
            props.put("language", new JSONObject().put("type", "string").put("description", "Language code, EN or DE"));
            props.put("value",    new JSONObject().put("type", "string").put("description", "New text value"));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray()
                            .put("id").put("language").put("value"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String id    = arguments.getString("id");
            String lang  = arguments.getString("language").toUpperCase();
            String value = arguments.getString("value");

            IASResourceDataService svc = service();
            TextResource r = svc.getTextResource(id);

            if (lang.equals("DE")) r.setValueDE(value);
            else                   r.setValueEN(value);

            svc.updateTextResource(r);
            return "Updated TextResource " + id + " [" + lang + "] = " + value;
        }
    }
}
