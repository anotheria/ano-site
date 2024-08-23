package net.anotheria.anosite.config;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to configure localizations translation.
 */
@ConfigureMe(name = "ano-site-localization-auto-translation-token-config")
public class LocalizationAutoTranslationTokenConfig {

    /**
     * Logger.
     */
    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationAutoTranslationTokenConfig.class);

    /**
     * Instance.
     */
    private static LocalizationAutoTranslationTokenConfig INSTANCE;

    public LocalizationAutoTranslationTokenConfig() {
        try {
            ConfigurationManager.INSTANCE.configure(this);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("Configuration fail[" + e.getMessage() + "]. Relaying on defaults.");
        }
    }

    @Configure
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static LocalizationAutoTranslationTokenConfig getInstance() {

        if (INSTANCE == null) {
            synchronized (LocalizationAutoTranslationTokenConfig.class) {
                INSTANCE = new LocalizationAutoTranslationTokenConfig();
            }
        }

        return INSTANCE;
    }

    @Override
    public String toString() {
        return "LocalizationAutoTranslationTokenConfig{" +
                "token='" + token + '\'' +
                '}';
    }
}
