package net.anotheria.anosite.cms.action;

import net.anotheria.anosite.gen.shared.action.BaseToolsAction;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author asamoilich.
 */
public class LocalizationBundleExportMafAction extends BaseToolsAction {

    protected boolean isAuthorizationRequired() {
        return true;
    }

    public ActionCommand anoDocExecute(ActionMapping aMapping, HttpServletRequest aReq, HttpServletResponse aRes) throws Exception {
        aReq.setAttribute("languages", getSupportedLanguages());
        aReq.setAttribute("selectedLanguage", "AT");
        return aMapping.success();
    }

    @Override
    protected String getTitle() {
        return "LocalizationBundleExport";
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
