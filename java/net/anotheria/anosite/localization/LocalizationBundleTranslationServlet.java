package net.anotheria.anosite.localization;

import net.anotheria.anodoc.data.StringProperty;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet({"/TranslateLocalizationBundle"})
@MultipartConfig
public class LocalizationBundleTranslationServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LocalizationBundleTranslationServlet.class);

    private IASResourceDataService resourceDataService;
    private final IASGTranslationService translationService;
    private final LocalizationAutoTranslationConfig config;
    private final ExecutorService executorService;

    public LocalizationBundleTranslationServlet() {
        this.config = LocalizationAutoTranslationConfig.getInstance();
        this.translationService = new TranslationServiceFactory().create();
        this.executorService = Executors.newFixedThreadPool(10);

        try {
            resourceDataService = MetaFactory.get(IASResourceDataService.class);
        } catch (MetaFactoryException e) {
            log.error("Cannot initialize LocalizationBundleTranslationServlet");
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
            String localeTo = req.getParameter("localeTo");

            if (localeFrom.equals(localeTo)) {
                jsonResponse.addError("INPUT_ERROR", "localeFrom equals to localeTo");
            } else {
                String languageFrom = null;
                String languageTo = null;

                for (Map.Entry<String, String> entry : config.getLanguagesMap().entrySet()) {
                    if (entry.getKey().equals(localeFrom)) {
                        languageFrom = entry.getValue();
                    }
                    if (entry.getKey().equals(localeTo)) {
                        languageTo = entry.getValue();
                    }
                }

                if (languageFrom == null) {
                    jsonResponse.addError("CONFIG_ERROR", "Check ano-site-localization-auto-translation-config. Cannot find a normal language for locale: " + localeFrom);
                } else if (languageTo == null) {
                    jsonResponse.addError("CONFIG_ERROR", "Check ano-site-localization-auto-translation-config. Cannot find a normal language for locale: " + localeTo);
                } else {

                    String localizationBundleFrom = "messages_" + localeFrom;

                    String content = "";
                    LocalizationBundleDocument bundle = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(bundleId);
                    Enumeration<String> keys = bundle.getKeys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        if (key.equals(localizationBundleFrom)) {
                            content = bundle.getString(key);
                            break;
                        }
                    }

                    if (StringUtils.isEmpty(content)) {
                        jsonResponse.addError("INPUT_ERROR", "Cannot find any content in for provided locale.");
                    }


                    StringBuilder translated = new StringBuilder();
                    List<String> contentLines = Arrays.asList(content.trim().split("\n"));
                    int chunkSize = 10;
                    int maxPages = contentLines.size() / chunkSize;
                    List<String> translatedVector = new Vector<>();

                    CountDownLatch countDownLatch = new CountDownLatch(maxPages + 1);

                    for (int i = 0; i <= maxPages; i++) {
                        String finalLanguageFrom = languageFrom;
                        String finalLanguageTo = languageTo;

                        int finalI = i;
                        Runnable chunkThread = () -> {
                            StringBuilder contentToTranslate = new StringBuilder();
                            int fromIndex = finalI * chunkSize;
                            int toIndex = finalI * chunkSize + chunkSize;

                            if (toIndex > contentLines.size()) {
                                toIndex = contentLines.size();
                            }

                            List<String> subList = contentLines.subList(fromIndex, toIndex);
                            for (String s : subList) {
                                contentToTranslate.append(s).append("\n");
                            }
                            String translatedTemp = translationService.translate(finalLanguageFrom, finalLanguageTo, contentToTranslate.toString());
                            translatedVector.add(translatedTemp);
                            countDownLatch.countDown();
                        };

                        executorService.submit(chunkThread);
                    }

                    countDownLatch.await();

                    for (String s : translatedVector) {
                        String[] translatedLines = s.split("\n");
                        for (String line : translatedLines) {
                            translated.append(line).append("\n");
                        }
                    }

                    if (!StringUtils.isEmpty(translated)) {
                        JSONObject data = new JSONObject();
                        data.put("success", true);
                        data.put("originalText", content);
                        data.put("translatedText", translated);
                        jsonResponse.setData(data);
                    } else {
                        jsonResponse.addError("CANNOT_TRANSLATE", "Cannot translate a provided localization");
                    }
                }
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
            String translatedText = req.getParameter("translatedText");
            String targetLocale = req.getParameter("targetLocale");
            String bundleId = req.getParameter("bundleId");

            LocalizationBundleDocument bundle = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(bundleId);

            String localizationBundleTo = "messages_" + targetLocale;

            bundle.putStringProperty(new StringProperty(localizationBundleTo, translatedText));
            resourceDataService.updateLocalizationBundle(bundle);
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
