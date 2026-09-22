package net.anotheria.anosite.cms.mcp;

import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns an {@link McpTool} into the tool specification the MCP sdk serves.
 *
 * <p>{@link McpTool} stays jettison based on purpose: it is the extension point projects implement (see
 * {@code LocalizationTools} here, or the {@code *McpTools} classes in the cms projects), and there is no reason
 * to make all of them depend on the sdk's schema types. This class is the only place that bridges the two, by
 * converting jettison objects to the plain maps/lists the sdk serializes.
 */
final class McpToolAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(McpToolAdapter.class);

    private McpToolAdapter() {
    }

    /**
     * Builds the sdk specification for one tool.
     *
     * @param tool tool to expose
     * @return specification to hand to the server builder
     * @throws JSONException if the tool's input schema can't be read
     */
    static McpStatelessServerFeatures.SyncToolSpecification toSpecification(McpTool tool) throws JSONException {
        McpSchema.Tool schema = McpSchema.Tool.builder(tool.name(), toMap(tool.inputSchema()))
                .title(tool.name())
                .description(tool.description())
                .annotations(McpSchema.ToolAnnotations.builder()
                        //everything these tools touch lives in the cms' own storage
                        .openWorldHint(false)
                        .build())
                .build();

        return McpStatelessServerFeatures.SyncToolSpecification.builder()
                .tool(schema)
                .callHandler((context, request) -> invoke(tool, request))
                .build();
    }

    /**
     * Runs the tool and maps whatever it does to a tool result. Exceptions become error results rather than
     * bubbling into the transport: a failing tool is an answer to the caller, not a broken request.
     */
    private static McpSchema.CallToolResult invoke(McpTool tool, McpSchema.CallToolRequest request) {
        Map<String, Object> arguments = request.arguments() == null ? Map.of() : request.arguments();
        try {
            return result(tool.execute(toJson(arguments)), false);
        } catch (IllegalArgumentException | JSONException e) {
            //bad arguments - the caller can fix them and retry, so report the reason verbatim
            LOG.warn("Bad arguments for tool {}({}): {}", tool.name(), arguments, e.getMessage());
            return result(e.getMessage(), true);
        } catch (Exception e) {
            LOG.error("Tool {}({}) failed", tool.name(), arguments, e);
            return result(e.getClass().getSimpleName() + ": " + e.getMessage(), true);
        }
    }

    private static McpSchema.CallToolResult result(String text, boolean isError) {
        return McpSchema.CallToolResult.builder()
                .addTextContent(text == null ? "" : text)
                .isError(isError)
                .build();
    }

    // -------------------------------------------------------------------------
    // jettison <-> plain java conversion.
    //
    // The sdk serializes plain maps, lists and boxed primitives; jettison has its
    // own wrapper types for objects, arrays and null. Both directions are needed:
    // input schemas go out as maps, call arguments come in as maps.
    // -------------------------------------------------------------------------

    /** Converts a jettison object to a plain map, recursively. Key order is preserved. */
    static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> result = new LinkedHashMap<>();
        if (object == null)
            return result;

        @SuppressWarnings("unchecked")
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            result.put(key, fromJettison(object.get(key)));
        }
        return result;
    }

    private static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> result = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++)
            result.add(fromJettison(array.get(i)));

        return result;
    }

    private static Object fromJettison(Object value) throws JSONException {
        if (value == null || JSONObject.NULL.equals(value))
            return null;
        if (value instanceof JSONObject nested)
            return toMap(nested);
        if (value instanceof JSONArray nested)
            return toList(nested);

        return value;
    }

    /** Converts a plain map back to a jettison object, recursively. */
    static JSONObject toJson(Map<String, Object> map) throws JSONException {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet())
            result.put(entry.getKey(), toJettison(entry.getValue()));

        return result;
    }

    private static JSONArray toJson(List<?> list) throws JSONException {
        JSONArray result = new JSONArray();
        for (Object value : list)
            result.put(toJettison(value));

        return result;
    }

    private static Object toJettison(Object value) throws JSONException {
        if (value == null)
            return JSONObject.NULL;
        if (value instanceof Map<?, ?> nested) {
            @SuppressWarnings("unchecked")
            Map<String, Object> typed = (Map<String, Object>) nested;
            return toJson(typed);
        }
        if (value instanceof List<?> nested)
            return toJson(nested);

        return value;
    }
}
