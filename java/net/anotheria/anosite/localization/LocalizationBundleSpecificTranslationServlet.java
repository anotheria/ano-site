package net.anotheria.anosite.localization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.cms.translation.IASGTranslationService;
import net.anotheria.anosite.cms.translation.TranslationServiceFactory;
import net.anotheria.anosite.config.LocalizationAutoTranslationConfig;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleDocument;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.maf.json.JSONResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet({"/SpecificTranslateLocalizationBundle"})
@MultipartConfig
public class LocalizationBundleSpecificTranslationServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LocalizationBundleSpecificTranslationServlet.class);

    private IASResourceDataService resourceDataService;
    private final IASGTranslationService translationService;
    private final LocalizationAutoTranslationConfig config;

    public LocalizationBundleSpecificTranslationServlet() {
        this.config = LocalizationAutoTranslationConfig.getInstance();
        this.translationService = new TranslationServiceFactory().create();

        try {
            resourceDataService = MetaFactory.get(IASResourceDataService.class);
        } catch (MetaFactoryException e) {
            log.error("Cannot initialize LocalizationBundleSpecificTranslationServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String method = req.getParameter("method");
        if (method.equals("translate")) {
            translate(req, resp);
        } else {
            saveTranslation(req, resp);
        }
    }

    private void translate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONResponse jsonResponse = new JSONResponse();
        try {
            String bundleId = req.getParameter("bundleId");
            String localeFrom = req.getParameter("localeFrom");
            String input = req.getParameter("input");

            if (StringUtils.isEmpty(input)) {
                jsonResponse.addError("INPUT_ERROR", "input field is empty");
            } else {
                String languageFrom = null;

                for (Map.Entry<String, String> entry : config.getLanguagesMap().entrySet()) {
                    if (entry.getKey().equals(localeFrom)) {
                        languageFrom = entry.getValue();
                    }
                }

                LocalizationBundleDocument bundle = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(bundleId);
                List<String> targetLocales = new LinkedList<>();

                Enumeration<String> keys = bundle.getKeys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();
                    if (key.contains("messages_") && !key.equals("messages_" + localeFrom)) {
                        targetLocales.add(key.substring(key.indexOf("_") + 1));
                    }
                }

                Map<String, String> translatedMap = new HashMap<>();
                for (String targetLocale : targetLocales) {
                    String language = config.getLanguagesMap().get(targetLocale);
                    if (language == null) {
                        jsonResponse.addError("CONFIG_ERROR", "Check ano-site-localization-auto-translation-config. Cannot find a normal language for locale: " + targetLocale);
                    }
                    String translatedContent = translationService.translate(languageFrom, language, input);
                    translatedMap.put(targetLocale, translatedContent);
                }

                JSONObject data = new JSONObject();
                data.put("success", true);
                data.put("results", translatedMap);
                jsonResponse.setData(data);
            }
        } catch (Exception any) {
            log.error(any.getMessage(), any);
            jsonResponse.addError("SERVER_ERROR", "Server error, please check logs.");
        }
        writeResponse(resp, jsonResponse.toJSON().toString());
    }

    private void saveTranslation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONResponse jsonResponse = new JSONResponse();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String bundleId = req.getParameter("bundleId");
            String rawTranslatedMap = req.getParameter("map");

            Map<String, String> translatedMap = new HashMap<>();
            ArrayNode rootNode = (ArrayNode) objectMapper.readTree(rawTranslatedMap);
            for (JsonNode jsonNode : rootNode) {
                ArrayNode value = (ArrayNode) jsonNode;
                String locale = value.get(0).asText().replace(":", "");
                String content = value.get(1).asText();
                translatedMap.put(locale, content);
            }

            LocalizationBundleDocument bundle = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(bundleId);
            for (Map.Entry<String, String> entry : translatedMap.entrySet()) {
                String existingContent = bundle.getString("messages_" + entry.getKey());
                existingContent = net.anotheria.util.StringUtils.removeChar(existingContent, '\r');
                Map<String, String> existingContentMap = net.anotheria.util.StringUtils.buildParameterMap(existingContent);

                String translatedContent = entry.getValue();
                translatedContent = net.anotheria.util.StringUtils.removeChar(translatedContent, '\r');
                Map<String, String> translatedContentMap = net.anotheria.util.StringUtils.buildParameterMap(translatedContent);

                for (Map.Entry<String, String> translatedEntry : translatedContentMap.entrySet()) {
                    if (existingContentMap.containsKey(translatedEntry.getKey())) {
                        existingContentMap.replace(translatedEntry.getKey(), translatedEntry.getValue());
                    } else {
                        existingContentMap.put(translatedEntry.getKey(), translatedEntry.getValue());
                    }
                }

                StringBuilder toSave = new StringBuilder();
                for (Map.Entry<String, String> existingEntry : existingContentMap.entrySet()) {
                    toSave.append(existingEntry.getKey()).append("=").append(existingEntry.getValue()).append("\n");
                }
                bundle.setString("messages_" + entry.getKey(), toSave.toString());
                resourceDataService.updateLocalizationBundle(bundle);
            }
        } catch (Exception any) {
            log.error(any.getMessage(), any);
            jsonResponse.addError("SERVER_ERROR", "Server error, please check logs.");
        }
        writeResponse(resp, jsonResponse.toJSON().toString());
    }

    protected void writeResponse(HttpServletResponse response, String jsonString) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(jsonString);
        writer.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    }

}
