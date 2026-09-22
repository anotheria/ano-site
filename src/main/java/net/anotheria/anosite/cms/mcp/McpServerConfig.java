package net.anotheria.anosite.cms.mcp;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Configuration of the MCP server exposed by {@link McpBootstrap}.
 *
 * <p>The server is disabled by default: a project only gets an MCP endpoint if it ships an
 * {@code ano-site-mcp-server-config.json} that turns it on. That is deliberate — the tools write to the CMS,
 * so an endpoint nobody configured must not exist.
 *
 * <p>MCP clients are machines, not CMS users, so they don't carry a CMS login. They authenticate with a shared
 * api key instead, checked by {@link McpApiKeyFilter}.
 */
@ConfigureMe(name = "ano-site-mcp-server-config")
public final class McpServerConfig implements Serializable {

    @DontConfigure
    private static final long serialVersionUID = 1L;

    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(McpServerConfig.class);

    @DontConfigure
    private static McpServerConfig INSTANCE;

    /**
     * If false, no MCP servlet is registered at all.
     */
    @Configure
    private boolean enabled = false;

    /**
     * Accepted api keys. A request has to present one of them in the {@code X-API-Key} header, or in the
     * {@code apiKey} request parameter for clients that can't set headers.
     */
    @Configure
    private String[] apiKeys = new String[0];

    /**
     * Url pattern prefix the MCP servlet is mapped under. Clients post to this very path — streamable http is a
     * single endpoint, there is no separate sse url anymore.
     */
    @Configure
    private String servletPath = "/mcp";

    /**
     * Rejected above this many bytes, so a runaway client can't push the CMS into an OOM.
     */
    @Configure
    private int maxRequestSizeBytes = 1024 * 1024;

    /**
     * Author recorded on everything the tools write. The cms ui puts the logged in user here; mcp clients have
     * no cms user, so all their changes are attributed to this name.
     */
    @Configure
    private String author = "mcp";

    /**
     * Server name reported to clients on initialize. Projects running several CMS instances should make this
     * distinguishable, it is what shows up in the client's server list.
     */
    @Configure
    private String serverName = "AnoSite CMS";

    /**
     * Server version reported to clients on initialize.
     */
    @Configure
    private String serverVersion = "1.0.0";

    /**
     * Free text handed to the client on initialize, describing what this CMS is and how to use its tools.
     * Empty means no instructions are sent.
     */
    @Configure
    private String instructions = "";

    /**
     * Get instance method.
     *
     * @return {@link McpServerConfig}
     */
    public static synchronized McpServerConfig getInstance() {
        if (INSTANCE == null)
            INSTANCE = new McpServerConfig();

        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    private McpServerConfig() {
        try {
            ConfigurationManager.INSTANCE.configure(this);
            LOGGER.info("McpServerConfig() Configured. Configuration[" + this + "].");
        } catch (Exception e) {
            LOGGER.warn("McpServerConfig() Configuration failed. Configuring with defaults[" + this + "].");
        }
    }

    /**
     * Returns true if the given key is one of the configured api keys. Compares in constant time, the presented
     * key comes straight off the wire.
     *
     * @param apiKey key presented by the client
     * @return true if the key is accepted
     */
    public boolean isValidApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty())
            return false;

        byte[] presented = apiKey.getBytes(StandardCharsets.UTF_8);
        boolean valid = false;
        for (String configured : apiKeys) {
            if (configured != null && !configured.isEmpty()
                    && MessageDigest.isEqual(configured.getBytes(StandardCharsets.UTF_8), presented))
                valid = true;
        }
        return valid;
    }

    /**
     * Url pattern the transport servlet and its api key filter are mapped under.
     *
     * @return servlet mapping covering the whole endpoint
     */
    public String getServletMapping() {
        return servletPath + "/*";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean aEnabled) {
        this.enabled = aEnabled;
    }

    public String[] getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(final String[] aApiKeys) {
        this.apiKeys = aApiKeys;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(final String aServletPath) {
        this.servletPath = aServletPath;
    }

    public int getMaxRequestSizeBytes() {
        return maxRequestSizeBytes;
    }

    public void setMaxRequestSizeBytes(final int aMaxRequestSizeBytes) {
        this.maxRequestSizeBytes = aMaxRequestSizeBytes;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String aAuthor) {
        this.author = aAuthor;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(final String aServerName) {
        this.serverName = aServerName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(final String aServerVersion) {
        this.serverVersion = aServerVersion;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(final String aInstructions) {
        this.instructions = aInstructions;
    }

    @Override
    public String toString() {
        return "McpServerConfig{" +
                "enabled=" + enabled +
                ", apiKeys=" + apiKeys.length + " configured" +
                ", servletPath='" + servletPath + '\'' +
                ", maxRequestSizeBytes=" + maxRequestSizeBytes +
                ", author='" + author + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverVersion='" + serverVersion + '\'' +
                '}';
    }
}
