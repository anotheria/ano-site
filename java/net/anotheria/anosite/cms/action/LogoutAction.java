package net.anotheria.anosite.cms.action;

import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.webutils.actions.AccessControlMafAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class LogoutAction extends AccessControlMafAction {

    @Override
    public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
        removeBeanFromSession(req, BEAN_USER_ID);
        res.addCookie(createAuthCookie(req, "CAFE", "BABE"));
        return mapping.success();
    }


}
 