package net.anotheria.anosite.cms.action.localizationbundle;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.shared.action.BaseToolsAction;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.util.NumberUtils;

import java.util.List;

/**
 * LocalizationBundleCompareMafAction — compare 2 localization bundles.
 *
 * @author ykalapusha
 * @since 27.01.2026
 */
public class LocalizationBundleCompareMafAction extends BaseToolsAction {

    private IASResourceDataService iasResourceDataService;

    public LocalizationBundleCompareMafAction(){
        try {
            this.iasResourceDataService = MetaFactory.get(IASResourceDataService.class);
        } catch (MetaFactoryException ex) {
            throw new RuntimeException("Cannot initialize LocalizationBundleCompareMafAction", ex);
        }
    }

    @Override
    protected String getTitle() {
        return "LocalizationBundleCompare";
    }

    @Override
    public ActionCommand anoDocExecute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
        List<LocalizationBundle> localizationBundles = iasResourceDataService.getLocalizationBundles();
        localizationBundles.sort(((o1, o2) -> {
            String firstId = NumberUtils.itoa(Integer.parseInt(o1.getId()), 3);
            String secondId = NumberUtils.itoa(Integer.parseInt(o2.getId()), 3);
            return firstId.compareToIgnoreCase(secondId);
        }));
        req.setAttribute("localizationBundles", localizationBundles);
        return mapping.success();
    }

    @Override
    protected String getCurrentModuleDefName() {
        return null;
    }

    @Override
    protected String getCurrentDocumentDefName() {
        return null;
    }
}
