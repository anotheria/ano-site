package net.anotheria.anosite.cms.mcp.tools;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anosite.cms.mcp.McpTool;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * MCP tools for reading and editing localization bundles.
 * Each inner class is one tool.
 */
public final class LocalizationTools {

    private LocalizationTools() {}

    private static IASResourceDataService service() throws Exception {
        return MetaFactory.get(IASResourceDataService.class);
    }

    // -------------------------------------------------------------------------

    public static class ListBundles implements McpTool {

        @Override public String name() { return "localization_list_bundles"; }
        @Override public String description() { return "List all localization bundles with their id and name."; }

        @Override public JSONObject inputSchema() throws JSONException {
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject())
                    .put("required", new org.codehaus.jettison.json.JSONArray());
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            List<LocalizationBundle> bundles = service().getLocalizationBundles();
            StringBuilder sb = new StringBuilder();
            for (LocalizationBundle b : bundles)
                sb.append(b.getId()).append("  ").append(b.getName()).append("\n");
            return sb.toString().trim();
        }
    }

    // -------------------------------------------------------------------------

    public static class GetBundle implements McpTool {

        @Override public String name() { return "localization_get_bundle"; }
        @Override public String description() {
            return "Get all key=value pairs from a localization bundle for a given language (EN or DE).";
        }

        @Override public JSONObject inputSchema() throws JSONException {
            JSONObject props = new JSONObject();
            props.put("id",       new JSONObject().put("type", "string").put("description", "Bundle id"));
            props.put("language", new JSONObject().put("type", "string").put("description", "Language code, e.g. EN or DE"));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray().put("id").put("language"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String id   = arguments.getString("id");
            String lang = arguments.getString("language").toUpperCase();
            LocalizationBundle bundle = service().getLocalizationBundle(id);
            return lang.equals("DE") ? bundle.getMessagesDE() : bundle.getMessagesEN();
        }
    }

    // -------------------------------------------------------------------------

    public static class SetBundleKey implements McpTool {

        @Override public String name() { return "localization_set_key"; }
        @Override public String description() {
            return "Add or update a single key in a localization bundle for a given language. " +
                   "The messages field is a .properties-style string (key=value per line).";
        }

        @Override public JSONObject inputSchema() throws JSONException {
            JSONObject props = new JSONObject();
            props.put("id",       new JSONObject().put("type", "string").put("description", "Bundle id"));
            props.put("language", new JSONObject().put("type", "string").put("description", "Language code, e.g. EN or DE"));
            props.put("key",      new JSONObject().put("type", "string").put("description", "Property key"));
            props.put("value",    new JSONObject().put("type", "string").put("description", "Property value"));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray()
                            .put("id").put("language").put("key").put("value"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String id    = arguments.getString("id");
            String lang  = arguments.getString("language").toUpperCase();
            String key   = arguments.getString("key");
            String value = arguments.getString("value");

            IASResourceDataService svc = service();
            LocalizationBundle bundle  = svc.getLocalizationBundle(id);

            String current = lang.equals("DE") ? bundle.getMessagesDE() : bundle.getMessagesEN();
            String updated = setKey(current, key, value);

            if (lang.equals("DE")) bundle.setMessagesDE(updated);
            else                   bundle.setMessagesEN(updated);

            svc.updateLocalizationBundle(bundle);
            return "Set " + key + "=" + value + " in bundle " + id + " [" + lang + "]";
        }

        private String setKey(String propertiesContent, String key, String value) throws Exception {
            Properties props = new Properties();
            if (propertiesContent != null && !propertiesContent.isBlank())
                props.load(new StringReader(propertiesContent));
            props.setProperty(key, value);
            StringWriter sw = new StringWriter();
            props.store(sw, null);
            // strip the timestamp comment Properties.store() adds
            return sw.toString().lines()
                    .filter(l -> !l.startsWith("#"))
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");
        }
    }

    // -------------------------------------------------------------------------

    public static class SetBundleKeys implements McpTool {

        @Override public String name() { return "localization_set_keys"; }
        @Override public String description() {
            return "Add or update multiple keys in a single localization bundle for a given language in one storage call. " +
                   "Use this instead of repeated localization_set_key calls to avoid hitting GCP write limits.";
        }

        @Override public JSONObject inputSchema() throws JSONException {
            JSONObject entriesSchema = new JSONObject()
                    .put("type", "object")
                    .put("description", "Map of property key → value pairs to set")
                    .put("additionalProperties", new JSONObject().put("type", "string"));
            JSONObject props = new JSONObject();
            props.put("id",       new JSONObject().put("type", "string").put("description", "Bundle id"));
            props.put("language", new JSONObject().put("type", "string").put("description", "Language code, e.g. EN or DE"));
            props.put("entries",  entriesSchema);
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray()
                            .put("id").put("language").put("entries"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String id      = arguments.getString("id");
            String lang    = arguments.getString("language").toUpperCase();
            JSONObject entries = arguments.getJSONObject("entries");

            IASResourceDataService svc = service();
            LocalizationBundle bundle  = svc.getLocalizationBundle(id);

            String current = lang.equals("DE") ? bundle.getMessagesDE() : bundle.getMessagesEN();
            Properties props = new Properties();
            if (current != null && !current.isBlank())
                props.load(new StringReader(current));

            @SuppressWarnings("unchecked")
            Iterator<String> keys = entries.keys();
            int count = 0;
            while (keys.hasNext()) {
                String key = keys.next();
                props.setProperty(key, entries.getString(key));
                count++;
            }

            StringWriter sw = new StringWriter();
            props.store(sw, null);
            String updated = sw.toString().lines()
                    .filter(l -> !l.startsWith("#"))
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");

            if (lang.equals("DE")) bundle.setMessagesDE(updated);
            else                   bundle.setMessagesEN(updated);

            svc.updateLocalizationBundle(bundle);
            return "Set " + count + " key(s) in bundle " + id + " [" + lang + "]";
        }
    }
}
