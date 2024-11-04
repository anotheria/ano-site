package net.anotheria.anosite.cms.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleDocument;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.json.JSONResponse;
import net.anotheria.util.StringUtils;
import net.anotheria.webutils.actions.BaseAction;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * For now, sort only lines in localization bundle.
 *
 * @author ykalapusha
 */
public class SortTextDataAction extends BaseAction {
    /**
     * {@link IASResourceDataService} instance.
     */
    private final IASResourceDataService iasResourceDataService;

    /**
     * Default constructor.
     */
    public SortTextDataAction() {
        try {
            iasResourceDataService = MetaFactory.get(IASResourceDataService.class);
        } catch (MetaFactoryException e) {
            log.error("Cannot initialize LocalizationBundleSortLinesServlet");
            throw new RuntimeException("Cannot initialize LocalizationBundleSortLinesServlet", e);
        }
    }

    @Override
    public ActionCommand execute(ActionMapping actionMapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONResponse response = new JSONResponse();
        try {
            String doc = req.getParameter("doc");
            String pId = req.getParameter("pId");
            if (StringUtils.isEmpty(pId) || StringUtils.isEmpty(doc)) {
                log.error("Cannot sort localization bundles data because pId or doc is empty");
                response.addError("Cannot sort localization bundles data because pId or doc is empty");
            } else {
                boolean isUpdated;
                switch (doc) {
                    case "LocalizationBundle":
                        isUpdated = sortLocalizationBundle(pId);
                        break;
                    default:
                        response.addError("Can not sort data for document: " + doc);
                        writeTextToResponse(res, response);
                        return null;
                }
                JSONObject data = new JSONObject();
                data.put("isUpdated", isUpdated);
                response.setData(data);
            }
        } catch (Exception e) {
            log.error("Cannot sort document data", e);
            response.addError("Cannot sort document. See logs");
        }
        writeTextToResponse(res, response);
        return null;
    }

    private boolean sortLocalizationBundle(String pId) throws Exception {
        LocalizationBundleDocument bundle = (LocalizationBundleDocument) iasResourceDataService.getLocalizationBundle(pId);
        Enumeration<String> keys = bundle.getKeys();
        boolean isForUpdate = false;
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.startsWith("messages_") && !StringUtils.isEmpty(bundle.getString(key))) {
                String messages = bundle.getString(key);
                List<String> lines = Arrays.asList(messages.split("\n"));
                Collections.sort(lines);
                StringBuilder sb = new StringBuilder();
                for (String line: lines) {
                    sb.append(line).append("\n");
                }
                bundle.setString(key, sb.toString());
                isForUpdate = true;
            }
        }

        if (isForUpdate) {
            iasResourceDataService.updateLocalizationBundle(bundle);
            return true;
        } else {
            return false;
        }
    }

    private void writeTextToResponse(final HttpServletResponse res, final JSONResponse jsonResponse) throws IOException, JSONException {
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        PrintWriter writer = res.getWriter();
        writer.write(jsonResponse.toString());
        writer.flush();
    }
}
