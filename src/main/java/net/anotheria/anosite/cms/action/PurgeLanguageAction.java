package net.anotheria.anosite.cms.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anosite.gen.shared.action.BaseToolsAction;
import net.anotheria.asg.service.ASGService;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

import java.util.List;

/**
 * For now we extends BaseToolsAction, but only to get access to the service instances in the base action.
 */
public class PurgeLanguageAction extends BaseToolsAction {
    @Override
    protected String getTitle() {
        return "";
    }

    @Override
    public ActionCommand anoDocExecute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
        System.out.println("Executing PurgeLanguageAction");
        String language = req.getParameter("language");
        System.out.println("Purging language: " + language);

        List<ASGService> services = getAllServices();
        for (ASGService service : services) {
            System.out.println("Purging language from service: " + service);
            service.purgeLanguageFromAllObjects(language);
        }


        return null;
    }

    @Override
    protected String getCurrentModuleDefName() {
        return "";
    }

    @Override
    protected String getCurrentDocumentDefName() {
        return "";
    }
}
