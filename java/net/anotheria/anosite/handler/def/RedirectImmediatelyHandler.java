package net.anotheria.anosite.handler.def;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.handler.AbstractBoxHandler;
import net.anotheria.anosite.handler.BoxHandlerResponse;
import net.anotheria.anosite.handler.ResponseRedirectImmediately;
import net.anotheria.anosite.handler.exception.BoxSubmitException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static net.anotheria.util.StringUtils.*;

/**
 * This handler sends a redirect request back to the browser. The redirect target is stored in the parameter1 of the associated box. This is used as builtin redirect mechanism.
 *
 * @author lrosenberg
 */
public class RedirectImmediatelyHandler extends AbstractBoxHandler {

	@Override
	public BoxHandlerResponse process(HttpServletRequest req, HttpServletResponse res, Box box, BoxBean bean) {
		return getRedirect(req, res, box);
	}

	@Override
	public BoxHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, Box box) throws BoxSubmitException {
		return getRedirect(req, res, box);
	}

	/**
	 * Reads the redirect target from the box.
	 *
	 * @param req  request
	 * @param res  response
	 * @param box  Anotheria BOX :)
	 * @return  response
	 */
	private BoxHandlerResponse getRedirect(HttpServletRequest req, HttpServletResponse res, Box box) {
		String urlQuery = req.getQueryString();
		String redirectTarget = box.getParameter1();
		String separator = redirectTarget.contains("?") ? "&" : "?";
		separator = !isEmpty(urlQuery) && urlQuery.startsWith(separator) ? "" : separator;
		String redirectURL = isEmpty(urlQuery) ? redirectTarget : redirectTarget + separator + urlQuery;
		return new ResponseRedirectImmediately(redirectURL);
	}
}
