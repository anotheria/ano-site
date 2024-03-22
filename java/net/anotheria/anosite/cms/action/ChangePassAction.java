package net.anotheria.anosite.cms.action;

import net.anotheria.anosite.cms.user.CMSUserManager;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.webutils.actions.BaseAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author vbezuhlyi
 * @see LoginAction
 * @see CMSUserManager
 */
public class ChangePassAction extends BaseAction {

    private static final String LOGIN_PAGE_PATH = "/cms/login";
    private static final String INDEX_PAGE_PATH = "/cms/index";

    private static final String P_IS_SUBMIT = "pIsSubmit";
    private static final String P_OLD_PASS = "pOldPass";
    private static final String P_NEW_PASS_1 = "pNewPass1";
    private static final String P_NEW_PASS_2 = "pNewPass2";

    private static final String BEAN_CHANGE_PASS_PAGE_MESSAGE = "Message";
    private static final String BEAN_USER_DEF_ID = "currentUserDefId";


    @Override
    public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {

        /* page is just opened (it's not submit) */
        String userId = (String)getBeanFromSession(req, BEAN_USER_DEF_ID);
        String login = CMSUserManager.getLoginById(userId);

        if (req.getParameter(P_IS_SUBMIT) == null) {
            /* for case when not logged user goes on ChangePass page directly */
            if (isAuthorizationRequired()){
                boolean authorized = checkAuthorization(req);
                if (!authorized){
                    res.sendRedirect(LOGIN_PAGE_PATH);
                    return null;
                }

                addBeanToRequest(req, BEAN_CHANGE_PASS_PAGE_MESSAGE, "Fill this fields to change password.");
                return mapping.findCommand("success");
            }
        }

        /* so, user have submitted ChangePass form */
        CMSUserManager manager = CMSUserManager.getInstance();
        String oldPass = req.getParameter(P_OLD_PASS);

        if (req.getParameter(P_IS_SUBMIT).equals("true") && manager.canLoginUser(login, oldPass)) {
            String newPass1 = req.getParameter(P_NEW_PASS_1);
            String newPass2 = req.getParameter(P_NEW_PASS_2);

            // new password field is empty
            if (newPass1.isEmpty()) {
                addBeanToRequest(req, BEAN_CHANGE_PASS_PAGE_MESSAGE, "Enter new password, please.");
                return mapping.findCommand("success");
            }

            // new password doesn't match with confirmed one
            if (!newPass1.equals(newPass2)) {
                addBeanToRequest(req, BEAN_CHANGE_PASS_PAGE_MESSAGE, "Entered password doesn't match with confirmed.");
                return mapping.findCommand("success");
            }

            // changing password
            CMSUserManager.changeUserPassword(login, newPass1);

            // scan users to update password (user should be able to login with new password from another browser)
            CMSUserManager.scanUsers();

            // redirect to index page (login with new password first will not be required while session is alive)
            res.sendRedirect(INDEX_PAGE_PATH);
            return null;
        }
        addBeanToRequest(req, BEAN_CHANGE_PASS_PAGE_MESSAGE, "Incorrect current password.");
        return mapping.findCommand("success");
    }


    protected boolean checkAuthorization(HttpServletRequest req){
        String userId = (String)getBeanFromSession(req, BEAN_USER_ID);
        return userId != null;
    }


    protected boolean isAuthorizationRequired(){
        return true;
    }

}

/* TODO:
 * In future it's better to generate ChangePassAction and LoginAction to make it possible to extend them
 * from BaseAnositeAction which class name is generated depending on project name (e.g. BaseAnositeExampleProjectAction),
 * so duplicated attribute BEAN_USER_DEF_ID would be in single place. Thus, creation of cookies and it's reading methods
 * should be moved directly into LoginAction to remove dependency from AccessControlMafAction,
 * that allows to extend LoginAction from BaseAnositeAction.
 */

