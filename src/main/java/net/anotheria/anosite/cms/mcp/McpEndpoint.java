package net.anotheria.anosite.cms.mcp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * MCP (Model Context Protocol) endpoint for the AnoSite CMS.
 *
 * Uses plain StreamingOutput + BlockingQueue for SSE instead of JAX-RS SseEventSink,
 * which has HK2 injection issues in this Jersey/servlet setup.
 *
 * Protocol:
 *   GET  /mcp/sse                      — open SSE stream, receive session id
 *   POST /mcp/messages?session={id}    — send JSON-RPC 2.0, responses arrive on stream
 */
@Path("/mcp")
public class McpEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(McpEndpoint.class);
    private static final String POISON = "__close__";
    private static final int HEARTBEAT_SECONDS = 30;

    private static final Map<String, BlockingQueue<String>> SESSIONS = new ConcurrentHashMap<>();
    private static final McpToolRegistry REGISTRY = new McpToolRegistry();

    public static void addTool(McpTool tool) {
        REGISTRY.register(tool);
    }

    public static void addBundle(List<McpTool> bundle) {
        REGISTRY.registerBundle(bundle);
    }

    @GET
    @Path("/sse")
    @Produces("text/event-stream")
    public void connect(@Context HttpServletRequest httpRequest,
                        @Context HttpServletResponse httpResponse) throws IOException {
        String sessionId = UUID.randomUUID().toString();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        SESSIONS.put(sessionId, queue);
        LOG.info("MCP client connected, session={}", sessionId);

        httpResponse.setContentType("text/event-stream");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setHeader("Cache-Control", "no-cache");
        httpResponse.setHeader("Connection", "keep-alive");

        // Derive the messages URL from the incoming request so the correct
        // servlet prefix (e.g. /asg-api) is included in the callback path.
        String messagesPath = httpRequest.getRequestURI().replace("/sse", "/messages")
                              + "?session=" + sessionId;

        Writer writer = httpResponse.getWriter();
        try {
            writeEvent(writer, "endpoint", messagesPath);

            while (!Thread.currentThread().isInterrupted()) {
                String event = queue.poll(HEARTBEAT_SECONDS, TimeUnit.SECONDS);
                if (event == null) {
                    writer.write(": ping\n\n");
                    writer.flush();
                } else if (POISON.equals(event)) {
                    break;
                } else {
                    writer.write(event);
                    writer.flush();
                }
            }
        } catch (Exception e) {
            LOG.info("MCP SSE stream closed, session={}", sessionId);
        } finally {
            SESSIONS.remove(sessionId);
            LOG.info("MCP client disconnected, session={}", sessionId);
        }
    }

    @POST
    @Path("/messages")
    @Consumes(MediaType.APPLICATION_JSON)
    public void handle(@QueryParam("session") String sessionId, String body) {
        BlockingQueue<String> queue = SESSIONS.get(sessionId);
        if (queue == null) {
            LOG.warn("Received message for unknown session={}", sessionId);
            return;
        }

        try {
            JSONObject req = new JSONObject(body);
            String method  = req.getString("method");
            Object id      = req.opt("id");

            JSONObject result = switch (method) {
                case "initialize" -> REGISTRY.initialize();
                case "tools/list" -> REGISTRY.toolsList();
                case "tools/call" -> REGISTRY.toolCall(req.getJSONObject("params"));
                default           -> new JSONObject().put("error", "Unknown method: " + method);
            };

            JSONObject response = new JSONObject()
                    .put("jsonrpc", "2.0")
                    .put("id", id)
                    .put("result", result);

            queue.offer(sseEvent("message", response.toString()));

        } catch (Exception e) {
            LOG.error("Failed to handle MCP message for session={}", sessionId, e);
        }
    }

    private static void writeEvent(Writer writer, String eventName, String data) throws Exception {
        writer.write(sseEvent(eventName, data));
        writer.flush();
    }

    private static String sseEvent(String eventName, String data) {
        return "event: " + eventName + "\ndata: " + data + "\n\n";
    }
}
