package net.anotheria.anosite.config;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Gets domain name we
 *
 * @author Vlad Lukjanenko
 */
@ConfigureMe(name = "ano-site-document-transfer-config")
public final class DocumentTransferConfig implements Serializable {

    /**
     * Logger.
     */
    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentTransferConfig.class);

    /**
     * Instance.
     * */
    private static DocumentTransferConfig INSTANCE;

    /**
     * Name of target point we need send document to.
     * */
    @Configure
    private String[] domains;


    private DocumentTransferConfig() {

        try {
            ConfigurationManager.INSTANCE.configure(this);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("Configuration fail[" + e.getMessage() + "]. Relaying on defaults.");
        }
    }


    public static DocumentTransferConfig getInstance() {

        if (INSTANCE == null) {
            synchronized (DocumentTransferConfig.class) {
                INSTANCE = new DocumentTransferConfig();
            }
        }

        return INSTANCE;
    }

    public String[] getDomains() {
        return domains;
    }

    public void setDomains(String[] domains) {
        this.domains = domains;
    }
}
