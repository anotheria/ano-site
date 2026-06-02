package net.anotheria.anosite.localization;

import net.anotheria.anodoc.data.StringProperty;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleDocument;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.maf.json.JSONResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author asamoilich.
 */
@WebServlet({"/ImportLocalizationBundle"})
@MultipartConfig
public class LocalizationBundleImportServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationBundleImportServlet.class);
    private IASResourceDataService resourceDataService;

    public LocalizationBundleImportServlet() {
        try {
            resourceDataService = MetaFactory.get(IASResourceDataService.class);
        } catch (MetaFactoryException e) {
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONResponse jsonResponse = new JSONResponse();
        try {
            Part file = request.getPart("file");
            String locale = request.getParameter("locale");
            String result = IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8);
            String[] values = result.split("\n");

            Map<String, Map<String, String>> bundlesMap = new HashMap<>();
            String prevKey = "";
            boolean duplicateKey = false;
            for (String row : values) {
                try {
                    String bundleId = row.substring(0, row.indexOf("."));
                    String keyValuePair = row.substring(row.indexOf(".") + 1);
                    String key = prevKey;
                    String value = keyValuePair;
                    if (keyValuePair.contains("=") && keyValuePair.indexOf("=") > 0) {
                        String slesh = keyValuePair.substring(keyValuePair.indexOf("=") - 1, keyValuePair.indexOf("="));
                        if (!slesh.equals("\\")) {
                            key = keyValuePair.substring(0, keyValuePair.indexOf("="));
                            duplicateKey = prevKey.equals(key);
                            prevKey = key;
                            value = keyValuePair.length() > key.length() + 1 ? keyValuePair.substring(keyValuePair.indexOf("=") + 1) : "";
                        }
                    }

                    if (!bundlesMap.containsKey(bundleId)) {
                        bundlesMap.put(bundleId, new HashMap<>());
                    }

                    Map<String, String> keyValueMap = bundlesMap.get(bundleId);
                    if (keyValueMap.containsKey(key) && !duplicateKey) {
                        keyValueMap.put(key, keyValueMap.get(key) + "\n" + value);
                    } else {
                        keyValueMap.put(key, value);
                    }
                    bundlesMap.put(bundleId, keyValueMap);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    addErrorToJsonResponse(jsonResponse, RESPONSE.SERVER_ERROR);
                    writeResponse(response, jsonResponse.toJSON().toString());
                    return;
                }
            }
            StringBuilder oldLocalizationNotUpdated = new StringBuilder();
            List<LocalizationBundle> bundlesToUpdate = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> entry : bundlesMap.entrySet()) {
                Map<String, String> newKeyValuePairs = entry.getValue();
                LocalizationBundle localizationBundle;
                try {
                    localizationBundle = resourceDataService.getLocalizationBundle(entry.getKey());
                } catch (ASResourceDataServiceException e) {
                    LOGGER.error(e.getMessage());
                    addErrorToJsonResponse(jsonResponse, RESPONSE.SERVER_ERROR);
                    continue;
                }
                LocalizationBundleDocument bundleDocument = (LocalizationBundleDocument) localizationBundle;
                StringProperty stringProperty = bundleDocument.getStringProperty("messages_" + locale);
                if (stringProperty == null || stringProperty.getString() == null) {
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> keyValuePair : newKeyValuePairs.entrySet()) {
                        sb.append(keyValuePair.getKey()).append("=").append(keyValuePair.getValue()).append("\n");
                    }
                    stringProperty = new StringProperty("messages_" + locale, sb.toString());
                    bundleDocument.putStringProperty(stringProperty);
                    bundlesToUpdate.add(bundleDocument);
                    continue;
                }
                StringBuilder keyValuePairsToUpdate = new StringBuilder();

                boolean alreadyUpdated = false;
                for (String row : stringProperty.getString().split("\n")) {
                    row = row.trim();
                    if (!row.isEmpty()) {
                        if (!row.contains("=") || (row.indexOf("=") > 0 && "\\".equals(row.substring(row.indexOf("=") - 1, row.indexOf("="))))) {
                            if (!alreadyUpdated)
                                keyValuePairsToUpdate.append(row).append("\n");
                            continue;
                        }

                        alreadyUpdated = false;
                        String key = row.substring(0, row.indexOf("="));
                        String oldValue = row.length() > key.length() + 1 ? row.substring(row.indexOf("=") + 1) : "";
                        if (newKeyValuePairs.containsKey(key)) {
                            String newValue = newKeyValuePairs.remove(key);
                            keyValuePairsToUpdate.append(key).append("=").append(newValue).append("\n");
                            alreadyUpdated = true;
                        } else {
                            keyValuePairsToUpdate.append(key).append("=").append(oldValue).append("\n");
                            oldLocalizationNotUpdated.append(entry.getKey()).append(".").append(key).append("=").append(oldValue).append("\n");
                        }
                    }
                }
                if (!newKeyValuePairs.isEmpty()) {
                    for (Map.Entry<String, String> kv : newKeyValuePairs.entrySet()) {
                        keyValuePairsToUpdate.append(kv.getKey()).append("=").append(kv.getValue()).append("\n");
                    }
                }
                stringProperty = new StringProperty("messages_" + locale, keyValuePairsToUpdate.toString());
                bundleDocument.putStringProperty(stringProperty);
                bundlesToUpdate.add(bundleDocument);
            }
            try {
                resourceDataService.updateLocalizationBundles(bundlesToUpdate);
            } catch (ASResourceDataServiceException e) {
                LOGGER.error(e.getMessage());
                addErrorToJsonResponse(jsonResponse, RESPONSE.SERVER_ERROR);
            }
            LOGGER.info("-------------------------------------------------------");
            LOGGER.info("--------  not imported/replaced localizations  --------");
            LOGGER.info(oldLocalizationNotUpdated.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            addErrorToJsonResponse(jsonResponse, RESPONSE.SERVER_ERROR);
        }
        writeResponse(response, jsonResponse.toJSON().toString());
    }

    private void writeResponse(HttpServletResponse response, String jsonString) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(jsonString);
        out.flush();
    }

    private void addErrorToJsonResponse(JSONResponse jsonResponse, RESPONSE response) {
        jsonResponse.addError(response.name(), response.errorMessage);
    }

    private enum RESPONSE {
        SERVER_ERROR("Error on file uploading."),
        NOT_MULTIPART_DATA,
        VALIDATION_FAIL("Data is not valid.");

        private String errorMessage;

        RESPONSE(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        RESPONSE() {
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }
}
