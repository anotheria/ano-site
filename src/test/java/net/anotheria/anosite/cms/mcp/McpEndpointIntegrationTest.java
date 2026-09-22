package net.anotheria.anosite.cms.mcp;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpStatelessSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStatelessServerTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.ContextManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Drives the real sdk transport servlet with real json-rpc requests, so the wiring the cms actually deploys -
 * transport, server, tool adapter - is exercised end to end rather than one layer at a time.
 */
public class McpEndpointIntegrationTest {

    private HttpServletStatelessServerTransport transport;
    private McpStatelessSyncServer server;
    private RecordingTool tool;

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
     * Stands in for a cms tool: remembers what it was called with and which thread ran it.
     */
    private static final class RecordingTool implements McpTool {

        private JSONObject received;
        private Thread executedOn;

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
                    .put("entries", new JSONObject().put("type", "object"));
            return new JSONObject()
                    .put("type", "object")
                    .put("properties", properties)
                    .put("required", new JSONArray().put("id"));
        }

        @Override
        public String execute(JSONObject arguments) {
            received = arguments;
            executedOn = Thread.currentThread();
            return "echoed " + arguments.optString("id");
        }
    }

    @Before
    public void setUp() throws Exception {
        tool = new RecordingTool();
        transport = HttpServletStatelessServerTransport.builder()
                .messageEndpoint("/mcp")
                .build();

        //the same server McpBootstrap builds, with one test tool instead of the registry's
        server = McpServer.sync(transport)
                .serverInfo("AnoSite CMS", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder().tools(false).build())
                .validateToolInputs(true)
                .immediateExecution(true)
                .tools(McpToolAdapter.toSpecification(tool))
                .build();
    }

    @After
    public void tearDown() {
        if (server != null)
            server.closeGracefully();
    }

    // -------------------------------------------------------------------------
    // Servlet stubs. The transport only touches a handful of methods, so proxies
    // are enough and save the test a container.
    // -------------------------------------------------------------------------

    private HttpServletRequest request(String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Map<String, String> headers = Map.of(
                "Accept", "application/json, text/event-stream",
                "Content-Type", "application/json");

        return (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getMethod" -> "POST";
                    case "getProtocol" -> "HTTP/1.1";
                    case "getHeader" -> headers.get(args[0]);
                    case "getHeaders" -> {
                        String value = headers.get(args[0]);
                        yield value == null ? Collections.emptyEnumeration()
                                : Collections.enumeration(List.of(value));
                    }
                    case "getHeaderNames" -> Collections.enumeration(headers.keySet());
                    case "getCharacterEncoding" -> "UTF-8";
                    case "getContentLengthLong" -> (long) bytes.length;
                    case "getRequestURI" -> "/mcp";
                    case "getInputStream" -> servletInputStream(bytes);
                    default -> null;
                });
    }

    private static ServletInputStream servletInputStream(byte[] bytes) {
        ByteArrayInputStream source = new ByteArrayInputStream(bytes);
        return new ServletInputStream() {
            @Override
            public int read() {
                return source.read();
            }

            @Override
            public boolean isFinished() {
                return source.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private final Map<String, Object> responseState = new HashMap<>();
    private StringWriter responseBody;

    private HttpServletResponse response() {
        responseBody = new StringWriter();
        responseState.clear();
        PrintWriter writer = new PrintWriter(responseBody);

        return (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "setStatus" -> responseState.put("status", args[0]);
                    case "sendError" -> responseState.put("status", args[0]);
                    case "setContentType" -> responseState.put("contentType", args[0]);
                    case "getWriter" -> writer;
                    default -> null;
                });
    }

    /**
     * Posts one json-rpc request to the transport servlet and returns the parsed response.
     */
    private JSONObject post(String body) throws Exception {
        HttpServletResponse response = response();
        transport.service(request(body), response);
        response.getWriter().flush();
        return new JSONObject(responseBody.toString());
    }

    // -------------------------------------------------------------------------

    @Test
    public void initializeNegotiatesAndReportsTheServer() throws Exception {
        JSONObject response = post("""
                {"jsonrpc":"2.0","id":1,"method":"initialize","params":{
                  "protocolVersion":"2025-06-18",
                  "capabilities":{},
                  "clientInfo":{"name":"test-client","version":"1.0.0"}}}""");

        assertEquals("2.0", response.getString("jsonrpc"));
        assertEquals(1, response.getInt("id"));

        JSONObject result = response.getJSONObject("result");
        assertEquals("AnoSite CMS", result.getJSONObject("serverInfo").getString("name"));
        //the version is negotiated now, not hardcoded the way the old endpoint answered it
        assertEquals("2025-06-18", result.getString("protocolVersion"));
        assertTrue(result.getJSONObject("capabilities").has("tools"));
    }

    /**
     * 2024-11-05 is the version of the deprecated http+sse transport this endpoint used to speak. A stateless
     * streamable http transport can't serve it, so the server answers with a version it does support and leaves
     * it to the client to decide whether it can go on. Clients pinned to 2024-11-05 have to be repointed.
     */
    @Test
    public void aClientAskingForTheRetiredSseVersionGetsASupportedOneBack() throws Exception {
        JSONObject response = post("""
                {"jsonrpc":"2.0","id":1,"method":"initialize","params":{
                  "protocolVersion":"2024-11-05",
                  "capabilities":{},
                  "clientInfo":{"name":"old-client","version":"1.0.0"}}}""");

        String negotiated = response.getJSONObject("result").getString("protocolVersion");
        assertFalse("the retired sse protocol version must not be offered", "2024-11-05".equals(negotiated));
        assertTrue(negotiated, negotiated.startsWith("2025-"));
    }

    @Test
    public void toolsListReturnsTheRegisteredTools() throws Exception {
        JSONObject response = post("{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/list\"}");

        JSONArray tools = response.getJSONObject("result").getJSONArray("tools");
        assertEquals(1, tools.length());
        assertEquals("echo", tools.getJSONObject(0).getString("name"));
        assertEquals("object", tools.getJSONObject(0).getJSONObject("inputSchema").getString("type"));
    }

    @Test
    public void toolsCallReachesTheToolAndComesBackAsText() throws Exception {
        JSONObject response = post("""
                {"jsonrpc":"2.0","id":3,"method":"tools/call","params":{
                  "name":"echo",
                  "arguments":{"id":"bundle-1","entries":{"greeting":"hallo"}}}}""");

        JSONObject result = response.getJSONObject("result");
        assertFalse(result.optBoolean("isError"));
        assertEquals("echoed bundle-1", result.getJSONArray("content").getJSONObject(0).getString("text"));
        //nested arguments have to reach the tool as json, this is what the adapter is for
        assertEquals("hallo", tool.received.getJSONObject("entries").getString("greeting"));
    }

    @Test
    public void toolsRunOnTheRequestThreadSoTheCallContextIsTheirs() throws Exception {
        post("""
                {"jsonrpc":"2.0","id":4,"method":"tools/call","params":{
                  "name":"echo","arguments":{"id":"x"}}}""");

        //immediateExecution(true) - the cms tools read the thread local CallContext for the current language
        //and the author, and that only exists on the thread the request came in on
        assertEquals(Thread.currentThread(), tool.executedOn);
    }

    @Test
    public void argumentsAreValidatedAgainstTheToolSchema() throws Exception {
        JSONObject response = post("""
                {"jsonrpc":"2.0","id":5,"method":"tools/call","params":{
                  "name":"echo","arguments":{}}}""");

        //the required id is missing, so the tool is never reached
        assertTrue(response.has("error") || response.getJSONObject("result").optBoolean("isError"));
    }

    @Test
    public void anUnknownMethodIsAJsonRpcErrorNotASuccess() throws Exception {
        JSONObject response = post("{\"jsonrpc\":\"2.0\",\"id\":6,\"method\":\"does/notExist\"}");

        //the old hand written endpoint answered this with a result containing an "error" key
        assertFalse("an unknown method must not come back as a success", response.has("result"));
        assertEquals(-32601, response.getJSONObject("error").getInt("code"));
    }

    @Test
    public void anUnknownToolIsReportedAsAnError() throws Exception {
        JSONObject response = post("""
                {"jsonrpc":"2.0","id":7,"method":"tools/call","params":{
                  "name":"no_such_tool","arguments":{}}}""");

        assertTrue(response.has("error") || response.getJSONObject("result").optBoolean("isError"));
    }
}
