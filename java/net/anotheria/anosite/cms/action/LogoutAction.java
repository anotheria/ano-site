package net.anotheria.anosite.cms.action;

import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class LogoutAction extends AccessControlAction {

    @Override
    public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
        removeBeanFromSession(req, BEAN_USER_ID);
        res.addCookie(createAuthCookie(req, "CAFE", "BABE"));
        return mapping.success();
    }


}
 