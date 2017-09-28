package net.anotheria.anosite.cms.action;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.access.AnoSiteAccessAPI;
import net.anotheria.maf.action.ActionForward;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.bean.FormBean;
import net.anotheria.util.StringUtils;
import net.anotheria.webutils.actions.AccessControlMafAction;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vbezuhlyi
 * @see LogoutAction
 * @see ChangePassAction
 */
public class LoginAction extends AccessControlMafAction {

    public static final String P_USER_LOGIN = "pUserLogin";
    public static final String P_PASSWORD = "pPassword";

    private static final String BEAN_USER_DEF_ID = "currentUserDefId";

    private AnoSiteAccessAPI anoSiteAccessAPI;

    public LoginAction() {
        anoSiteAccessAPI = APIFinder.findAPI(AnoSiteAccessAPI.class);
    }

    public ActionForward execute(ActionMapping mapping, FormBean bean, HttpServletRequest req, HttpServletResponse res) throws Exception {

        /* first try to read auth from cookie */
        try {
            String authString = null;
            String cookieName = getAuthCookieName(req);
            for (Cookie c : req.getCookies()) {
                if (c != null && c.getName().equals(cookieName)) {
                    authString = getCryptTool().decryptFromHex(c.getValue()).trim();
                    break;
                }
            }

            if (authString != null) {
                int index = authString.indexOf(AUTH_DELIMITER);
                String login = authString.substring(0, index);
                String password = authString.substring(index + 1);
                String userId = anoSiteAccessAPI.getUserIdByLogin(login);

                if (StringUtils.isEmpty(login) && StringUtils.isEmpty(password)) {
                    if (anoSiteAccessAPI.canUserLogin(login, password)) {
                        /* CAUTION: HttpSession is serializable in Tomcat by default! */
                        addBeanToSession(req, BEAN_USER_ID, login);
                        addBeanToSession(req, BEAN_USER_DEF_ID, userId);
                        res.sendRedirect(getRedirectTarget(req));
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("read auth from cookie", e);
        }
        /* end of cookie auth */

        String login, password, userId;

        try {
            login = getStringParameter(req, P_USER_LOGIN);
            password = getStringParameter(req, P_PASSWORD);

            if (!anoSiteAccessAPI.canUserLogin(login, password))
                throw new RuntimeException("Can't login.");

            userId = anoSiteAccessAPI.getUserIdByLogin(login);
            res.addCookie(createAuthCookie(req, login, password));
        } catch (Exception e) {
            return mapping.findForward("success");
        }

        addBeanToSession(req, BEAN_USER_ID, login);
        addBeanToSession(req, BEAN_USER_DEF_ID, userId);
        res.sendRedirect(getRedirectTarget(req));
        return null;
    }


    private String getRedirectTarget(HttpServletRequest req) {
        String authParam = "auth=true";
        String redT = (String) req.getSession().getAttribute(BEAN_TARGET_ACTION);
        if (redT == null) {
            String servletPath = req.getServletPath();
            servletPath = servletPath.substring(0, servletPath.lastIndexOf('/'));
            return req.getContextPath() + servletPath + "/index?" + authParam;
        }

        if (!(redT.startsWith("/")))
            redT = "/" + redT;
        if (redT.indexOf('?') == -1)
            redT += "?";
        else
            redT += "&";
        redT += authParam;
        return redT;
    }


}

/* TODO:
 * In future it's better to generate ChangePassAction and LoginAction to make it possible to extend them
 * from BaseAnositeAction which class name is generated depending on project name (e.g. BaseAnositeExampleProjectAction),
 * so duplicated attribute BEAN_USER_DEF_ID would be in single place. Thus, creation of cookies and it's reading methods
 * should be moved directly into LoginAction to remove dependency from AccessControlMafAction,
 * that allows to extend LoginAction from BaseAnositeAction.
 */