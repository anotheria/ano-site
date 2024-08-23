package net.anotheria.anosite.config;

import com.google.gson.annotations.SerializedName;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to configure localizations translation.
 */
@ConfigureMe(name = "ano-site-localization-auto-translation-config")
public class LocalizationAutoTranslationConfig {

    /**
     * Logger.
     */
    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationAutoTranslationConfig.class);

    /**
     * Instance.
     */
    private static LocalizationAutoTranslationConfig INSTANCE;

    /**
     * OpenAI is used to make an auto-translation.
     * OpenAI token.
     */
    @Configure
    private String openAIToken;

    /**
     * Languages or localizations that are used in system, but mapped with normal names,
     * for example: EN - english, FR - french, CH_DE - switzer german.
     */
    @Configure
    @SerializedName("@languages")
    private LocalizationAutoTranslationConfig.LanguageParameter[] languages;

    /**
     * Map that is populated from list 'languages'
     */
    @DontConfigure
    private Map<String, String> languagesMap = new HashMap<>();

    private LocalizationAutoTranslationConfig() {
        try {
            ConfigurationManager.INSTANCE.configure(this);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("Configuration fail[" + e.getMessage() + "]. Relaying on defaults.");
        }

        for (LocalizationAutoTranslationConfig.LanguageParameter language : this.languages) {
            this.languagesMap.put(language.key, language.value);
        }
    }

    public String getOpenAIToken() {
        return openAIToken;
    }

    public void setOpenAIToken(String openAIToken) {
        this.openAIToken = openAIToken;
    }

    public void setLanguages(LocalizationAutoTranslationConfig.LanguageParameter[] languages) {
        this.languages = languages;
    }

    public Map<String, String> getLanguagesMap() {
        return languagesMap;
    }

    public void setLanguagesMap(Map<String, String> languagesMap) {
        this.languagesMap = languagesMap;
    }

    public static LocalizationAutoTranslationConfig getInstance() {

        if (INSTANCE == null) {
            synchronized (LocalizationAutoTranslationConfig.class) {
                INSTANCE = new LocalizationAutoTranslationConfig();
            }
        }

        return INSTANCE;
    }

    @Override
    public String toString() {
        return "LocalizationAutoTranslationConfig{" +
                "openAIToken='" + openAIToken + '\'' +
                ", languagesMapConfig=" + languages +
                '}';
    }

    @ConfigureMe
    public static class LanguageParameter {

        /**
         * CMS localization name
         * For example: EN
         */
        @Configure
        private String key;

        /**
         * Normal AI-friendly name
         * For example: english
         */
        @Configure
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "LanguageParameter{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

}
