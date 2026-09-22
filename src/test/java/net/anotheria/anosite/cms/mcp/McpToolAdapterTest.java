package net.anotheria.anosite.cms.mcp;

import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.ContextManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the bridge between the jettison based {@link McpTool} and the sdk's tool specifications.
 */
public class McpToolAdapterTest {

    /**
     * The built in tools build their input schema from the project's languages, so the tool registry can only
     * be asked for its specifications once a call context factory is in place - which is what a real project's
     * context listener does before {@link McpBootstrap} runs.
     */
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
            return List.of("EN", "DE");
        }
    }

    /**
     * A tool that hands back whatever arguments it got, so the test can see what arrived on the other side.
     */
    private static class EchoTool implements McpTool {

        private JSONObject received;

        @Override
        public String name() {
            return "echo";
        }

        @Override
        public String description() {
            return "Echoes its arguments.";
        }

        @Override
        public JSONObject inputSchema() throws JSONException {
            JSONObject properties = new JSONObject()
                    .put("id", new JSONObject().put("type", "string"))
                    .put("entries", new JSONObject()
                            .put("type", "object")
                            .put("additionalProperties", new JSONObject().put("type", "string")));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", properties)
                    .put("required", new JSONArray().put("id"));
        }

        @Override
        public String execute(JSONObject arguments) throws Exception {
            received = arguments;
            return "ok";
        }
    }

    private static McpSchema.CallToolResult call(McpTool tool, Map<String, Object> arguments) throws JSONException {
        McpStatelessServerFeatures.SyncToolSpecification specification = McpToolAdapter.toSpecification(tool);
        return specification.callHandler().apply(null, new McpSchema.CallToolRequest("echo", arguments));
    }

    @Test
    public void inputSchemaSurvivesTheConversion() throws Exception {
        McpSchema.Tool tool = McpToolAdapter.toSpecification(new EchoTool()).tool();

        assertEquals("echo", tool.name());
        assertEquals("Echoes its arguments.", tool.description());
        assertEquals("object", tool.inputSchema().get("type"));
        assertEquals(List.of("id"), tool.inputSchema().get("required"));

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) tool.inputSchema().get("properties");
        assertTrue("nested schema objects have to survive as maps", properties.get("entries") instanceof Map);
    }

    @Test
    public void nestedArgumentsArriveAsJettisonObjects() throws Exception {
        EchoTool tool = new EchoTool();
        McpSchema.CallToolResult result = call(tool, Map.of(
                "id", "bundle-1",
                "entries", Map.of("greeting", "hallo"),
                "languages", List.of("EN", "DE")));

        assertFalse(Boolean.TRUE.equals(result.isError()));
        assertEquals("bundle-1", tool.received.getString("id"));
        //this is the case a flat map copy breaks: the tool reads entries as a json object, not as a raw map
        assertEquals("hallo", tool.received.getJSONObject("entries").getString("greeting"));
        assertEquals("DE", tool.received.getJSONArray("languages").getString(1));
    }

    @Test
    public void nullArgumentsBecomeJsonNull() throws Exception {
        EchoTool tool = new EchoTool();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", null);

        call(tool, arguments);

        assertTrue(tool.received.isNull("id"));
    }

    @Test
    public void missingArgumentsAreReportedAsAToolError() throws Exception {
        McpTool failing = new EchoTool() {
            @Override
            public String execute(JSONObject arguments) throws Exception {
                //what the real tools do: read a required argument straight off the json object
                return arguments.getString("missing");
            }
        };

        McpSchema.CallToolResult result = call(failing, Map.of("id", "x"));

        assertTrue("a failing tool is an answer to the caller, not a broken request", result.isError());
    }

    @Test
    public void toolExceptionsBecomeErrorResultsInsteadOfEscaping() throws Exception {
        McpTool failing = new EchoTool() {
            @Override
            public String execute(JSONObject arguments) {
                throw new IllegalStateException("storage is down");
            }
        };

        McpSchema.CallToolResult result = call(failing, Map.of("id", "x"));

        assertTrue(result.isError());
        McpSchema.TextContent content = (McpSchema.TextContent) result.content().get(0);
        assertTrue(content.text(), content.text().contains("storage is down"));
    }

    @Test
    public void everyRegisteredToolCanBeSpecified() throws Exception {
        List<McpStatelessServerFeatures.SyncToolSpecification> specifications =
                McpToolRegistry.shared().specifications();

        assertEquals(McpToolRegistry.shared().tools().size(), specifications.size());
        for (McpStatelessServerFeatures.SyncToolSpecification specification : specifications) {
            McpSchema.Tool tool = specification.tool();
            assertNotEmpty("name", tool.name());
            assertNotEmpty("description of " + tool.name(), tool.description());
            assertNotNull("schema of " + tool.name(), tool.inputSchema());
            assertEquals("schema of " + tool.name(), "object", tool.inputSchema().get("type"));
        }
    }

    @Test
    public void languageSchemasAreBuiltFromTheProjectsLanguages() throws Exception {
        McpSchema.Tool getBundle = McpToolRegistry.shared().specifications().stream()
                .map(McpStatelessServerFeatures.SyncToolSpecification::tool)
                .filter(tool -> "localization_get_bundle".equals(tool.name()))
                .findFirst()
                .orElseThrow();

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) getBundle.inputSchema().get("properties");
        @SuppressWarnings("unchecked")
        Map<String, Object> language = (Map<String, Object>) properties.get("language");

        //schemas are built once at startup, so they have to see the running project's languages by then
        assertEquals("Language code, one of: EN, DE", language.get("description"));
    }

    private static void assertNotEmpty(String what, String value) {
        assertTrue("expected a non empty " + what, value != null && !value.isEmpty());
    }
}
