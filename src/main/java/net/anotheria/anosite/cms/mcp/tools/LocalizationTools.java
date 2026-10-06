package net.anotheria.anosite.cms.mcp.tools;

import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anosite.cms.mcp.McpTool;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleFactory;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    // Language handling.
    //
    // The number of languages is not fixed: extending projects override
    // context.xml and can define many (e.g. baldur-cms has ~12). We must not
    // depend on the project-specific generated language-utils class name
    // (AnositeLanguageUtils, BaldurLanguageUtils, ...). Instead we ask the
    // thread-local CallContext, whose concrete (generated) implementation is
    // wired to the running project's languages. A bundle stores one message
    // column per language (messages_<LANG>); the generated getMessages()/
    // setMessages() resolve that column from the CallContext's current language.
    // So we validate against CallContext.getSupportedLanguages() and read/write
    // through the CallContext instead of hardcoding EN/DE.
    // -------------------------------------------------------------------------

    private static List<String> supportedLanguages() {
        return ContextManager.getCallContext().getSupportedLanguages();
    }

    /** Uppercase and validate the language against the project's supported languages. */
    private static String normalizeLanguage(String language) {
        String lang = language == null ? "" : language.trim().toUpperCase();
        List<String> supported = supportedLanguages();
        if (!supported.contains(lang))
            throw new IllegalArgumentException("Unsupported language '" + language
                    + "'. Supported languages: " + String.join(", ", supported));
        return lang;
    }

    private static String languageDescription() {
        return "Language code, one of: " + String.join(", ", supportedLanguages());
    }

    @FunctionalInterface
    private interface LangAction<T> { T run() throws Exception; }

    /** Run an action with the given language set on the thread-local CallContext, restoring it afterwards. */
    private static <T> T withLanguage(String language, LangAction<T> action) throws Exception {
        CallContext ctx = ContextManager.getCallContext();
        String previous = ctx.getCurrentLanguage();
        try {
            ctx.setCurrentLanguage(language);
            return action.run();
        } finally {
            ctx.setCurrentLanguage(previous);
        }
    }

    // -------------------------------------------------------------------------
    // Bundle message (de)serialization.
    //
    // The messages field is a plain line-based "key=value" string. Nothing is
    // escaped: the key is everything before the first '=', the value is the
    // rest of the line verbatim (so values may freely contain '=', ':', '!',
    // '@', non-ASCII, etc.). This matches how the CMS import/export servlets
    // (LocalizationBundleImportServlet / ...ExportToTxtAction) read and write
    // bundles. Using java.util.Properties was wrong because Properties.store()
    // adds backslash escapes before ':', '!', '#', spaces and non-ASCII, which
    // corrupts the stored messages.
    // -------------------------------------------------------------------------

    /** Parse a bundle message string into an ordered key -> value map. */
    private static Map<String, String> parse(String content) {
        Map<String, String> result = new LinkedHashMap<>();
        if (content == null || content.isBlank())
            return result;
        String lastKey = null;
        for (String line : content.replace("\r", "").split("\n", -1)) {
            if (line.isBlank())
                continue;
            int sep = line.indexOf('=');
            if (sep <= 0) {
                // no key -> continuation of the previous (multi-line) value
                if (lastKey != null)
                    result.put(lastKey, result.get(lastKey) + "\n" + line);
                continue;
            }
            String key   = line.substring(0, sep);
            String value = line.substring(sep + 1);
            result.put(key, value);
            lastKey = key;
        }
        return result;
    }

    /** Serialize an ordered key -> value map back to the bundle message format. */
    private static String serialize(Map<String, String> entries) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : entries.entrySet()) {
            if (sb.length() > 0)
                sb.append('\n');
            sb.append(e.getKey()).append('=').append(e.getValue());
        }
        return sb.toString();
    }

    /** Copy an {@code entries} tool argument into a bundle map, returning the number of entries copied. */
    private static int putAll(JSONObject entries, Map<String, String> target) throws JSONException {
        @SuppressWarnings("unchecked")
        Iterator<String> keys = entries.keys();
        int count = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            target.put(key, entries.getString(key));
            count++;
        }
        return count;
    }

    /** Schema for the {@code entries} argument shared by the create and set-keys tools. */
    private static JSONObject entriesSchema(String description) throws JSONException {
        return new JSONObject()
                .put("type", "object")
                .put("description", description)
                .put("additionalProperties", new JSONObject().put("type", "string"));
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
            return "Get all key=value pairs from a localization bundle for a given language.";
        }

        @Override public JSONObject inputSchema() throws JSONException {
            JSONObject props = new JSONObject();
            props.put("id",       new JSONObject().put("type", "string").put("description", "Bundle id"));
            props.put("language", new JSONObject().put("type", "string").put("description", languageDescription()));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray().put("id").put("language"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String id   = arguments.getString("id");
            String lang = normalizeLanguage(arguments.getString("language"));
            LocalizationBundle bundle = service().getLocalizationBundle(id);
            return withLanguage(lang, bundle::getMessages);
        }
    }

    // -------------------------------------------------------------------------

    public static class CreateBundle implements McpTool {

        @Override public String name() { return "localization_create_bundle"; }
        @Override public String description() {
            return "Create a new localization bundle and return its id. Optionally sets a parent bundle to " +
                   "inherit keys from, and initial key=value entries for one language. " +
                   "Refuses to create a second bundle under an existing name.";
        }

        @Override public JSONObject inputSchema() throws JSONException {
            JSONObject props = new JSONObject();
            props.put("name",         new JSONObject().put("type", "string")
                    .put("description", "Bundle name, has to be free"));
            props.put("parentBundle", new JSONObject().put("type", "string")
                    .put("description", "Optional id of the bundle to inherit keys from"));
            props.put("language",     new JSONObject().put("type", "string")
                    .put("description", languageDescription() + ". Required together with entries."));
            props.put("entries",      entriesSchema("Optional initial property key -> value pairs, set in the given language"));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray().put("name"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String name = arguments.getString("name").trim();
            if (name.isEmpty())
                throw new IllegalArgumentException("name must not be empty.");

            JSONObject entries = arguments.optJSONObject("entries");
            boolean hasEntries = entries != null && entries.length() > 0;
            String language = arguments.has("language") ? normalizeLanguage(arguments.getString("language")) : null;
            if (hasEntries && language == null)
                throw new IllegalArgumentException("language is required when entries are given. " + languageDescription());

            String parent = arguments.has("parentBundle") ? arguments.getString("parentBundle").trim() : "";

            IASResourceDataService svc = service();

            //One read serves both checks. Nothing in the cms enforces unique bundle names - bundles are linked
            //and resolved by id - but a second bundle under a name that is already taken is both the mistake a
            //caller makes most easily and the one it cannot undo from here, since there is no delete tool. So
            //we refuse it and name the bundle that is already there instead.
            boolean parentFound = parent.isEmpty();
            for (LocalizationBundle existing : svc.getLocalizationBundles()) {
                if (name.equalsIgnoreCase(existing.getName()))
                    throw new IllegalArgumentException("A localization bundle named '" + existing.getName()
                            + "' already exists (id " + existing.getId() + "). Use localization_set_key(s) to edit it.");
                if (!parent.isEmpty() && parent.equals(existing.getId()))
                    parentFound = true;
            }
            if (!parentFound)
                throw new IllegalArgumentException("No localization bundle with id '" + parent
                        + "' to use as parent bundle.");

            LocalizationBundle bundle = LocalizationBundleFactory.createLocalizationBundle();
            bundle.setName(name);
            if (!parent.isEmpty())
                bundle.setParentBundle(parent);

            int count = 0;
            if (hasEntries)
                count = withLanguage(language, () -> {
                    Map<String, String> props = new LinkedHashMap<>();
                    int c = putAll(entries, props);
                    bundle.setMessages(serialize(props));
                    return c;
                });

            //the id is assigned here, by the storage: the document the factory returns has none yet.
            LocalizationBundle created = svc.createLocalizationBundle(bundle);
            return "Created bundle " + created.getId() + " '" + name + "'"
                    + (parent.isEmpty() ? "" : ", parent bundle " + parent)
                    + (hasEntries ? " with " + count + " key(s) [" + language + "]" : " (empty)");
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
            props.put("language", new JSONObject().put("type", "string").put("description", languageDescription()));
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
            String lang  = normalizeLanguage(arguments.getString("language"));
            String key   = arguments.getString("key");
            String value = arguments.getString("value");

            IASResourceDataService svc = service();
            LocalizationBundle bundle  = svc.getLocalizationBundle(id);

            withLanguage(lang, () -> {
                Map<String, String> props = parse(bundle.getMessages());
                props.put(key, value);
                bundle.setMessages(serialize(props));
                return null;
            });

            svc.updateLocalizationBundle(bundle);
            return "Set " + key + "=" + value + " in bundle " + id + " [" + lang + "]";
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
            JSONObject props = new JSONObject();
            props.put("id",       new JSONObject().put("type", "string").put("description", "Bundle id"));
            props.put("language", new JSONObject().put("type", "string").put("description", languageDescription()));
            props.put("entries",  entriesSchema("Map of property key -> value pairs to set"));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", props)
                    .put("required", new org.codehaus.jettison.json.JSONArray()
                            .put("id").put("language").put("entries"));
        }

        @Override public String execute(JSONObject arguments) throws Exception {
            String id      = arguments.getString("id");
            String lang    = normalizeLanguage(arguments.getString("language"));
            JSONObject entries = arguments.getJSONObject("entries");

            IASResourceDataService svc = service();
            LocalizationBundle bundle  = svc.getLocalizationBundle(id);

            int count = withLanguage(lang, () -> {
                Map<String, String> props = parse(bundle.getMessages());
                int c = putAll(entries, props);
                bundle.setMessages(serialize(props));
                return c;
            });

            svc.updateLocalizationBundle(bundle);
            return "Set " + count + " key(s) in bundle " + id + " [" + lang + "]";
        }
    }
}
