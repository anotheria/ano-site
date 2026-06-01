
package net.anotheria.anosite.localization;

import net.anotheria.anodoc.data.StringProperty;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleDocument;
import net.anotheria.maf.json.JSONResponse;
import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/MakeParentLocalizationBundle"})
public class LocalizationBundleMakeParentServlet extends AbstractLocalizationParentServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONResponse jsonResponse = new JSONResponse();
        try {
            String parentName = request.getParameter("parentName");
            if (StringUtils.isEmpty(parentName)) {
                jsonResponse.addError("NAME", "Parent name is empty");
                writeResponse(response, jsonResponse.toJSON().toString());
                return;
            }
            String child1 = request.getParameter("child1");
            String child2 = request.getParameter("child2");
            LocalizationBundleDocument localizationBundle1 = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(child1);
            LocalizationBundleDocument localizationBundle2 = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(child2);
            LocalizationBundleDocument parent = new LocalizationBundleDocument();
            parent.setName(parentName);
            Enumeration<String> keys = localizationBundle1.getKeys();
            List<String> messagesKeys = new ArrayList<>();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                if (key.startsWith("messages")) {
                    messagesKeys.add(key);
                }
            }

            for (String messageKey : messagesKeys) {
                List<String> values1 = getLocalizationValuesByLocale(localizationBundle1, messageKey);
                List<String> values2 = getLocalizationValuesByLocale(localizationBundle2, messageKey);

                Map<String, String> keyValuesMap1 = getKeyValuePairsMap(values1);
                Map<String, String> keyValuesMap2 = getKeyValuePairsMap(values2);
                Map<String, String> parentMap = new HashMap<>();

                if (!values1.isEmpty() && !values2.isEmpty()) {
                    for (Map.Entry<String, String> entry : keyValuesMap1.entrySet()) {
                        if (keyValuesMap2.containsKey(entry.getKey()) && entry.getValue().equals(keyValuesMap2.get(entry.getKey()))) {
                            parentMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }

                for (String parentKey : parentMap.keySet()) {
                    keyValuesMap1.remove(parentKey);
                    keyValuesMap2.remove(parentKey);
                }

                parent.putStringProperty(new StringProperty(messageKey, geKeyValuePairsAsString(parentMap)));
                localizationBundle1.putStringProperty(new StringProperty(messageKey, geKeyValuePairsAsString(keyValuesMap1)));
                localizationBundle2.putStringProperty(new StringProperty(messageKey, geKeyValuePairsAsString(keyValuesMap2)));
            }

            LocalizationBundle createdBundle = resourceDataService.createLocalizationBundle(parent);
            localizationBundle1.setParentBundle(createdBundle.getId());
            localizationBundle2.setParentBundle(createdBundle.getId());
            resourceDataService.updateLocalizationBundle(localizationBundle1);
            resourceDataService.updateLocalizationBundle(localizationBundle2);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            jsonResponse.addError("SERVER_ERROR", "Server error, please check logs.");
        }
        writeResponse(response, jsonResponse.toJSON().toString());
    }
}
