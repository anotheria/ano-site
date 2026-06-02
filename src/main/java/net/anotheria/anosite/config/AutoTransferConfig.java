package net.anotheria.anosite.config;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Configuration of copying asg module data of specify environment to another directory.
 *
 * @author ykalapusha
 */
@ConfigureMe(name = "auto-transfer-config")
public final class AutoTransferConfig implements Serializable {
    /**
     * {@link Logger} instance.
     */
    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTransferConfig.class);
    /**
     * Synchronization lock.
     */
    @DontConfigure
    private static final Object LOCK = new Object();
    /**
     * {@link AutoTransferConfig} instance.
     */
    @DontConfigure
    private static volatile AutoTransferConfig instance;
    /**
     * Array of {@link AutoTransfer}.
     */
    @Configure
    private AutoTransfer[] autoTransfers;

    /**
     * Private constructor.
     */
    private AutoTransferConfig() {
        try {
            ConfigurationManager.INSTANCE.configure(this);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("AutoTransferConfig() configuration fail [" + e.getMessage() + "]");
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.warn("AutoTransferConfig() configured with [" + this.toString() + "]");
        }
    }

    /**
     * Get configuration instance.
     *
     * @return
     *      {@link AutoTransferConfig} instance
     */
    public static AutoTransferConfig getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new AutoTransferConfig();
                }
            }
        }

        return instance;
    }


    public AutoTransfer[] getAutoTransfers() {
        return autoTransfers;
    }

    public void setAutoTransfers(AutoTransfer[] autoTransfers) {
        this.autoTransfers = autoTransfers;
    }

    @Override
    public String toString() {
        return "AutoTransferConfig{" +
                "autoTransfers=" + Arrays.toString(autoTransfers) +
                '}';
    }
}
