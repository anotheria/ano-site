package net.anotheria.anosite.cms.action;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.shared.action.BaseToolsAction;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;


public class LocalizationBundleMakeParentsMafAction extends BaseToolsAction {

    private IASResourceDataService iasResourceDataService;

    public LocalizationBundleMakeParentsMafAction() {
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
        try {
            List<LocalizationBundle> localizationBundles = iasResourceDataService.getLocalizationBundles();
            localizationBundles.sort(Comparator.comparing(LocalizationBundle::getId));
            aReq.setAttribute("localizationBundles", localizationBundles);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return aMapping.success();
    }

    @Override
    protected String getTitle() {
        ;
        return "LocalizationBundleMakeParents";
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
