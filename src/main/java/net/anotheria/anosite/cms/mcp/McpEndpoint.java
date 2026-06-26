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
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MCP (Model Context Protocol) endpoint for the AnoSite CMS.
 *
 * <p>SSE is implemented with Jersey {@link ChunkedOutput} rather than a blocking
 * loop on the request thread. Returning the {@code ChunkedOutput} releases the
 * Tomcat worker thread immediately while Jersey keeps the connection open and
 * streams chunks as they are written. This is essential: with the previous
 * blocking model every open stream pinned one {@code http-nio-*-exec-*} worker,
 * so once the (default 200) pool was full the whole server stopped responding.
 *
 * <p>A single shared scheduler sends heartbeats and reaps dead or idle sessions.
 * Liveness is judged from real client traffic (POSTs to {@code /messages}),
 * because a heartbeat write to a half-open TCP connection can be buffered by the
 * OS and succeed without ever surfacing the broken pipe.
 *
 * Protocol:
 *   GET  /mcp/sse                      — open SSE stream, receive session id
 *   POST /mcp/messages?session={id}    — send JSON-RPC 2.0, responses arrive on stream
 */
@Path("/mcp")
public class McpEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(McpEndpoint.class);
    private static final int HEARTBEAT_SECONDS = 30;
    /** Force-close a session after this much time without any client→server traffic. */
    private static final long IDLE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(30);

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final McpToolRegistry REGISTRY = new McpToolRegistry();

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "McpEndpoint-Reaper");
                t.setDaemon(true);
                return t;
            });

    static {
        SCHEDULER.scheduleWithFixedDelay(McpEndpoint::heartbeatAndReap,
                HEARTBEAT_SECONDS, HEARTBEAT_SECONDS, TimeUnit.SECONDS);
    }

    /** One live SSE connection. The {@link ChunkedOutput} streams to the client
     *  without holding a Tomcat worker thread. */
    private static final class Session {
        final ChunkedOutput<String> output;
        /** Last time the client proved it is alive by POSTing to /messages. */
        volatile long lastClientActivity = System.currentTimeMillis();

        Session(ChunkedOutput<String> output) {
            this.output = output;
        }
    }

    public static void addTool(McpTool tool) {
        REGISTRY.register(tool);
    }

    public static void addBundle(List<McpTool> bundle) {
        REGISTRY.registerBundle(bundle);
    }

    @GET
    @Path("/sse")
    @Produces("text/event-stream;charset=UTF-8")
    public ChunkedOutput<String> connect(@Context HttpServletRequest httpRequest,
                                         @Context HttpServletResponse httpResponse) {
        String sessionId = UUID.randomUUID().toString();
        ChunkedOutput<String> output = new ChunkedOutput<>(String.class);
        SESSIONS.put(sessionId, new Session(output));
        LOG.info("MCP client connected, session={} ({} active)", sessionId, SESSIONS.size());

        httpResponse.setHeader("Cache-Control", "no-cache");
        // Disable proxy/Nginx response buffering so SSE chunks are flushed promptly.
        httpResponse.setHeader("X-Accel-Buffering", "no");

        // Derive the messages URL from the incoming request so the correct
        // servlet prefix (e.g. /asg-api) is included in the callback path.
        String messagesPath = httpRequest.getRequestURI().replace("/sse", "/messages")
                              + "?session=" + sessionId;
        try {
            output.write(sseEvent("endpoint", messagesPath));
        } catch (IOException e) {
            closeSession(sessionId, "failed to send endpoint event");
        }

        // Returning the ChunkedOutput hands the connection to Jersey and frees
        // this worker thread; subsequent events are written from other threads.
        return output;
    }

    @POST
    @Path("/messages")
    @Consumes(MediaType.APPLICATION_JSON)
    public void handle(@QueryParam("session") String sessionId, String body) {
        Session session = SESSIONS.get(sessionId);
        if (session == null) {
            LOG.warn("Received message for unknown session={}", sessionId);
            return;
        }
        session.lastClientActivity = System.currentTimeMillis();

        JSONObject response;
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

            response = new JSONObject()
                    .put("jsonrpc", "2.0")
                    .put("id", id)
                    .put("result", result);
        } catch (Exception e) {
            LOG.error("Failed to handle MCP message for session={}", sessionId, e);
            return;
        }

        try {
            session.output.write(sseEvent("message", response.toString()));
        } catch (IOException | IllegalStateException e) {
            closeSession(sessionId, "client stream closed");
        }
    }

    /** Sends periodic heartbeats and reaps connections that are dead (heartbeat
     *  write fails) or idle beyond {@link #IDLE_TIMEOUT_MS}. Runs on a single
     *  shared daemon thread, so connection count no longer drives thread usage. */
    private static void heartbeatAndReap() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Session> entry : SESSIONS.entrySet()) {
            String sessionId = entry.getKey();
            Session session = entry.getValue();
            if (now - session.lastClientActivity > IDLE_TIMEOUT_MS) {
                closeSession(sessionId, "idle timeout");
                continue;
            }
            try {
                session.output.write(": ping\n\n");
            } catch (IOException | IllegalStateException e) {
                closeSession(sessionId, "heartbeat failed");
            }
        }
    }

    private static void closeSession(String sessionId, String reason) {
        Session session = SESSIONS.remove(sessionId);
        if (session == null) {
            return;
        }
        try {
            session.output.close();
        } catch (IOException ignored) {
            // already closed by the container
        }
        LOG.info("MCP client disconnected, session={} ({}) ({} active)",
                sessionId, reason, SESSIONS.size());
    }

    private static String sseEvent(String eventName, String data) {
        return "event: " + eventName + "\ndata: " + data + "\n\n";
    }
}
