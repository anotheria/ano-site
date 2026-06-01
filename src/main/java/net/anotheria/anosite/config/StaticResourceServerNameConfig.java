package net.anotheria.anosite.config;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Selects the static resource server according to the environment.
 *
 * @author ykalapusha
 */
@ConfigureMe(name = "static-resource-server-config")
public final class StaticResourceServerNameConfig implements Serializable {

    /**
     * {@link Logger} instance.
     */
    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceServerNameConfig.class);

    /**
     * Synchronization lock.
     */
    @DontConfigure
    private static final Object LOCK = new Object();

    /**
     * {@link StaticResourceServerNameConfig} instance.
     */
    @DontConfigure
    private static volatile StaticResourceServerNameConfig instance;

    /**
     * Name of the static resource server.
     */
    @Configure
    private String nameResourceServer;

    /**
     * Private constructor.
     */
    private StaticResourceServerNameConfig() {
        try {
            ConfigurationManager.INSTANCE.configure(this);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("StaticResourceServerNameConfig() configuration fail [" + e.getMessage() + "]");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.warn("StaticResourceServerNameConfig() configured with [" + this.toString() + "]");
        }
    }

    /**
     * Get configuration instance.
     *
     * @return
     *      {@link StaticResourceServerNameConfig} instance
     */
    public static StaticResourceServerNameConfig getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if(instance == null) {
                    instance = new StaticResourceServerNameConfig();
                }
            }
        }
        return instance;
    }

    public String getNameResourceServer() {
        return nameResourceServer;
    }

    public void setNameResourceServer(String nameResourceServer) {
        this.nameResourceServer = nameResourceServer;
    }

    @Override
    public String toString() {
        return "StaticResourceServerNameConfig{"
                + "nameResourceServer='" + nameResourceServer + '\''
                + '}';
    }
}
