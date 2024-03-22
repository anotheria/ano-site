package net.anotheria.anosite.cms.action;

import net.anotheria.anodoc.data.StringProperty;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundleDocument;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.shared.action.BaseToolsAction;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author asamoilich.
 */
public class LocalizationBundleExportToTxtAction extends BaseToolsAction {

    private final IASResourceDataService iasResourceDataService;

    public LocalizationBundleExportToTxtAction() {
        try {
            iasResourceDataService = MetaFactory.get(IASResourceDataService.class);
        } catch (MetaFactoryException e) {
            throw new RuntimeException("Unable to create service", e);
        }
    }

    protected boolean isAuthorizationRequired() {
        return true;
    }

    public ActionCommand anoDocExecute(ActionMapping aMapping, HttpServletRequest aReq, HttpServletResponse aRes) throws Exception {
        String locale = aReq.getParameter("locale");
        try {
            List<LocalizationBundle> localizationBundles = iasResourceDataService.getLocalizationBundles();
            StringBuilder sb = new StringBuilder();
            for (LocalizationBundle bundle : localizationBundles) {
                if (bundle instanceof LocalizationBundleDocument) {
                    LocalizationBundleDocument bundleDocument = (LocalizationBundleDocument) bundle;
                    StringProperty stringProperty = bundleDocument.getStringProperty("messages_" + locale);
                    if (stringProperty == null || stringProperty.getString() == null) continue;
                    String messages = stringProperty.getString();
                    String[] values = messages.split("\n");

                    for (String row : values) {
                        row = row.trim();
                        if (!row.isEmpty()) {
                            sb.append(bundle.getId()).append(".").append(row).append("\n");
                        }
                    }
                }
            }
            aRes.getOutputStream().write((sb.toString()).getBytes(StandardCharsets.UTF_8));
            aRes.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getTitle() {
        return "LocalizationBundleExportToTxt";
    }

    @Override
    protected String getCurrentDocumentDefName() {
        return null;
    }

    @Override
    protected String getCurrentModuleDefName() {
        return null;
    }

}
