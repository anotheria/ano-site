package net.anotheria.anosite.localization;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleDocument;
import net.anotheria.anosite.gen.shared.service.AnositeLanguageUtils;
import net.anotheria.maf.json.JSONResponse;
import net.anotheria.util.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * LocalizationBundleCompareServlet — Servlet for comparing bundles.
 *
 * @author ykalapusha
 * @since 27.01.2026
 */
@WebServlet({"/LocalizationBundlesCompare"})
@MultipartConfig
public class LocalizationBundlesCompareServlet extends AbstractLocalizationParentServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JSONResponse jsonResponse = new JSONResponse();
        StringBuilder result = new StringBuilder();
        String firstBundleId = req.getParameter("firstBundle");
        String secondBundleId = req.getParameter("secondBundle");
        boolean isValid = true;

        try {
            if (StringUtils.isEmpty(firstBundleId)) {
                jsonResponse.addError("firstBundleId is required");
                isValid = false;
            }

            if (StringUtils.isEmpty(secondBundleId)) {
                jsonResponse.addError("secondBundleId is required");
                isValid = false;
            }

            if (Objects.equals(firstBundleId, secondBundleId)) {
                jsonResponse.addError("INPUT_ERROR", "First bundle equals to second bundle");
                isValid = false;
            }



            if (isValid) {
                LocalizationBundleDocument firstBundle = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(firstBundleId);
                LocalizationBundleDocument secondBundle = (LocalizationBundleDocument) resourceDataService.getLocalizationBundle(secondBundleId);

                if (firstBundle != null && secondBundle != null) {
                    for (String language : AnositeLanguageUtils.getSupportedLanguages()) {
                        StringBuilder firstMapDiffValues = new StringBuilder();
                        StringBuilder secondMapDiffValues = new StringBuilder();
                        Map<String, String> firstMap = getKeyValuePairsMap(getLocalizationValuesByLocale(firstBundle, "messages_" + language));
                        Map<String, String> secondMap = getKeyValuePairsMap(getLocalizationValuesByLocale(secondBundle, "messages_" + language));

                        Set<String> commonKeys = new HashSet<>();
                        commonKeys.addAll(firstMap.keySet());
                        commonKeys.addAll(secondMap.keySet());

                        for (String key : commonKeys) {
                            String firstValue = firstMap.get(key);
                            String secondValue = secondMap.get(key);

                            if (!Objects.equals(firstValue, secondValue)) {
                                firstMapDiffValues.append(key).append("=").append(firstValue != null ? firstValue : "").append("\n");
                                secondMapDiffValues.append(key).append("=").append(secondValue != null ? secondValue : "").append("\n");
                            }
                        }

                        if (!firstMapDiffValues.isEmpty() || !secondMapDiffValues.isEmpty()) {
                            result.append("==== ").append(language).append(" ====\n")
                                    .append(firstBundle.getName()).append("[").append(firstBundleId).append("]\n").append(firstMapDiffValues).append("\n")
                                    .append(secondBundle.getName()).append("[").append(secondBundleId).append("]\n").append(secondMapDiffValues).append("\n");
                        }

                    }
                } else {
                    jsonResponse.addError("INPUT_ERROR", "Cannot find bundles by provided ids.");
                    isValid = false;
                }
            }
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            jsonResponse.addError("SERVER_ERROR", "Server error, please check logs.");
        }

        if (isValid){
            String resultData = result.isEmpty() ? "Bundles are equal" : result.toString();
            jsonResponse.addRawData("result", resultData);
        }
        writeResponse(resp, jsonResponse.toJSON().toString());
    }
}
