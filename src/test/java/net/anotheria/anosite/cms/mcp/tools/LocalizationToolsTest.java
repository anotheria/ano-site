package net.anotheria.anosite.cms.mcp.tools;

import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.ContextManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests {@link LocalizationTools.CreateBundle}'s schema and the argument checks it makes before it touches the
 * storage. Everything past those checks needs a configured {@code IASResourceDataService}, so it is left to
 * the cms projects' own tests.
 */
public class LocalizationToolsTest {

    /**
     * The localization tools read the project's languages off the thread local call context, so a factory has
     * to be in place - which is what a real project's context listener does at startup.
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

    @Test
    public void schemaRequiresOnlyTheName() throws Exception {
        JSONObject schema = new LocalizationTools.CreateBundle().inputSchema();

        JSONArray required = schema.getJSONArray("required");
        assertEquals(1, required.length());
        assertEquals("name", required.getString(0));

        JSONObject properties = schema.getJSONObject("properties");
        assertTrue(properties.has("name"));
        assertTrue(properties.has("parentBundle"));
        assertTrue(properties.has("language"));
        assertTrue(properties.has("entries"));

        //the language description has to name the running project's languages, not a hardcoded EN/DE
        String languageDescription = properties.getJSONObject("language").getString("description");
        assertTrue(languageDescription, languageDescription.contains("EN"));
        assertTrue(languageDescription, languageDescription.contains("DE"));

        //entries is a free form key -> value map of strings
        JSONObject entries = properties.getJSONObject("entries");
        assertEquals("object", entries.getString("type"));
        assertEquals("string", entries.getJSONObject("additionalProperties").getString("type"));
    }

    @Test
    public void blankNameIsRejected() throws Exception {
        assertRejected(new JSONObject().put("name", "   "), "name must not be empty");
    }

    @Test
    public void entriesWithoutLanguageAreRejected() throws Exception {
        JSONObject arguments = new JSONObject()
                .put("name", "newbundle")
                .put("entries", new JSONObject().put("title", "Title"));
        assertRejected(arguments, "language is required");
    }

    @Test
    public void unsupportedLanguageIsRejected() throws Exception {
        JSONObject arguments = new JSONObject()
                .put("name", "newbundle")
                .put("language", "FR")
                .put("entries", new JSONObject().put("title", "Titre"));
        assertRejected(arguments, "Unsupported language");
    }

    /**
     * Asserts the tool refuses the given arguments with a message the caller can act on. The checks under test
     * all run before the tool looks up its service, so no storage is needed here.
     */
    private static void assertRejected(JSONObject arguments, String expectedMessagePart) throws Exception {
        try {
            new LocalizationTools.CreateBundle().execute(arguments);
            fail("Expected " + arguments + " to be rejected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(expectedMessagePart));
        }
    }
}
