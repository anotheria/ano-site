
package net.anotheria.anosite.localization;

import net.anotheria.anodoc.data.StringProperty;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleDocument;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLocalizationParentServlet extends HttpServlet {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractLocalizationParentServlet.class);
    protected IASResourceDataService resourceDataService;

    protected AbstractLocalizationParentServlet() {
        try {
            resourceDataService = MetaFactory.get(IASResourceDataService.class);
        } catch (MetaFactoryException e) {
        }
    }


    protected String geKeyValuePairsAsString(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }

    protected List<String> getLocalizationValuesByLocale(LocalizationBundleDocument bundleDocument, String messagesKey) {
        StringProperty stringProperty = bundleDocument.getStringProperty(messagesKey);
        if (stringProperty == null || stringProperty.getString() == null) return Collections.emptyList();
        String messages = stringProperty.getString();
        return messages.isEmpty() ? Collections.emptyList() : Arrays.asList(messages.split("\n"));
    }

    protected Map<String, String> getKeyValuePairsMap(List<String> values) throws Exception {
        Map<String, String> keyValueMap = new HashMap<>();
        String prevKey = "";
        boolean duplicateKey = false;
        for (String keyValuePair : values) {
            try {
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

                if (keyValueMap.containsKey(key) && !duplicateKey) {
                    keyValueMap.put(key, keyValueMap.get(key) + "\n" + value);
                } else {
                    keyValueMap.put(key, value);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                throw new Exception(e);
            }
        }
        return keyValueMap;
    }

    protected void writeResponse(HttpServletResponse response, String jsonString) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(jsonString);
        writer.flush();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }
}
