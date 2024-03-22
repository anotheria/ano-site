package net.anotheria.anosite.cms.action;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.shared.action.BaseToolsAction;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;

public class LocalizationBundleSpecificTranslationMafAction extends BaseToolsAction {

    private IASResourceDataService iasResourceDataService;

    private static Logger log = LoggerFactory.getLogger(LocalizationBundleSpecificTranslationMafAction.class);

    public LocalizationBundleSpecificTranslationMafAction() {
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
        List<LocalizationBundle> localizationBundles = iasResourceDataService.getLocalizationBundles();
        localizationBundles.sort(Comparator.comparing(LocalizationBundle::getId));
        aReq.setAttribute("localizationBundles", localizationBundles);
        aReq.setAttribute("languages", getSupportedLanguages());
        return aMapping.success();
    }

    @Override
    protected String getTitle() {
        return "LocalizationBundleSpecificTranslation";
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
