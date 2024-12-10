package net.anotheria.anosite.cms.action;

import net.anotheria.asg.util.bean.ErrorPageBean;
import net.anotheria.maf.action.Action;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Base action for generated sources.
 *
 * @author lrosenberg
 */
public abstract class BaseAction implements Action {
	/**
	 * {@link Logger} instance.
	 */
	protected Logger log;
	/**
	 * Attribute parameter: user id.
	 */
	protected static final String BEAN_USER_ID = "currentUserId";
	/**
	 * Attribute parameter: current action.
	 */
	protected static final String BEAN_TARGET_ACTION = "anoDocTargetAction";
	/**
	 *
	 */
	public static final String PARAM_ID = "pId";

	/**
	 * Constants for month names.
	 */
	public static final String[] MONTH = { "", "JAN", "FEB", "MAR", "APR", "MAI", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	/**
	 * Basic constructor.
	 */
	protected BaseAction() {
		log = LoggerFactory.getLogger(this.getClass());
	}


	protected ActionCommand handleError(HttpServletRequest req, HttpServletResponse res, Exception e, ActionMapping mapping) {
		ErrorPageBean errbean = new ErrorPageBean();
		errbean.setMessage(e.getMessage());
		errbean.setStackTrace(throwableToStrackTrace(e));
		addBeanToRequest(req, "error", errbean);
		return mapping.findCommand(getErrorPageForward());
	}

	@Override
	public void preProcess(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
	}
	
	@Override
	public void postProcess(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
	}


	protected void addBeanToSession(HttpServletRequest request, String key, Object value) {
		addBean(request, PageContext.SESSION_SCOPE, key, value);
	}

	protected void addBeanToApplication(HttpServletRequest request, String key, Object value) {
		addBean(request, PageContext.APPLICATION_SCOPE, key, value);
	}

	protected void addBeanToRequest(HttpServletRequest request, String key, Object value) {
		addBean(request, PageContext.REQUEST_SCOPE, key, value);
	}
	
	protected Object getBeanFromSession(HttpServletRequest request, String key) {
		return getBean(request, PageContext.SESSION_SCOPE, key);
	}

	protected Object getBeanFromApplication(HttpServletRequest request, String key) {
		return getBean(request, PageContext.APPLICATION_SCOPE, key);
	}

	protected Object getBeanFromRequest(HttpServletRequest request, String key) {
		return getBean(request, PageContext.REQUEST_SCOPE, key);
	}

	protected void removeBeanFromSession(HttpServletRequest request, String key) {
		removeBean(request, PageContext.SESSION_SCOPE, key);
	}

	protected void removeBeanFromApplication(HttpServletRequest request, String key) {
		removeBean(request, PageContext.APPLICATION_SCOPE, key);
	}

	protected void removeBeanFromRequest(HttpServletRequest request, String key) {
		removeBean(request, PageContext.REQUEST_SCOPE, key);
	}
	
	protected void addBean(HttpServletRequest request, int scope, String key, Object value) {
		switch (scope) {
		case PageContext.APPLICATION_SCOPE: {
			request.getSession().getServletContext().setAttribute(key, value);
			break;
		}
		case PageContext.SESSION_SCOPE: {
			request.getSession().setAttribute(key, value);
			break;
		}
		case PageContext.REQUEST_SCOPE:
			request.setAttribute(key, value);
			break;

		default: {
			throw new RuntimeException("Unknown scope:" + scope);
		}
		}
	}

	protected Object getBean(HttpServletRequest request, int scope, String key) {
		switch (scope) {
		case PageContext.APPLICATION_SCOPE: {
			return request.getSession().getServletContext().getAttribute(key);
		}
		case PageContext.SESSION_SCOPE: {
			return request.getSession().getAttribute(key);
		}
		case PageContext.REQUEST_SCOPE:
			return request.getAttribute(key);

		default: {
			throw new RuntimeException("Unknown scope:" + scope);
		}
		}
	}

	protected void removeBean(HttpServletRequest request, int scope, String key) {
		switch (scope) {
		case PageContext.APPLICATION_SCOPE: {
			request.getSession().getServletContext().removeAttribute(key);
			break;
		}
		case PageContext.SESSION_SCOPE: {
			request.getSession().removeAttribute(key);
			break;
		}
		case PageContext.REQUEST_SCOPE:
			request.removeAttribute(key);
			break;

		default: {
			throw new RuntimeException("Unknown scope:" + scope);
		}
		}
	}


	protected static String getStringParameter(HttpServletRequest request, String name) {
		String s = request.getParameter(name);
		if (s == null)
			throw new RuntimeException("Parameter " + name + " is not set.");
		return s;
	}

	protected static int getIntParameter(HttpServletRequest request, String name) {
		return Integer.parseInt(getStringParameter(request, name));
	}

	protected String getErrorPageForward() {
		return "ErrorPage";
	}

	protected String getUserId(HttpServletRequest req) {
		return (String) req.getSession().getAttribute(BEAN_USER_ID);
	}

	protected void setUserId(HttpServletRequest req, String userId) {
		req.getSession().setAttribute(BEAN_USER_ID, userId);
	}

	/**
	 * Often needed by different packages Return a time string in form of DD MMM
	 * YYYY (23 FEB 2002).
	 */
	public static String makeDateStringLong(long time) {
		Date date = new Date(time);
		return itoa(date.day) + " " + MONTH[date.month] + " " + date.year;
	}

	/**
	 * Often needed by different packages Return a time string in form of DD MMM
	 * YY (23 FEB 02).
	 */
	public static String makeDateString(long time) {
		Date date = new Date(time);
		int minus = date.year > 2000 ? 2000 : 1900;
		return itoa(date.day) + " " + MONTH[date.month] + " " + itoa(date.year - minus);
	}

	/**
	 * Returns a short digital date string (23.02.02).
	 * 
	 * @param time
	 * @return
	 */
	public static String makeDigitalDateStringShort(long time) {
		Date date = new Date(time);
		return itoa(date.day) + "." + itoa(date.month) + "." + itoa(date.year).substring(2);
	}

	/**
	 * Returns a digital time string (23.02.2002).
	 * 
	 * @param time
	 * @return
	 */
	public static String makeDigitalDateString(long time) {
		Date date = new Date(time);
		return itoa(date.day) + "." + itoa(date.month) + "." + itoa(date.year);
	}

	/**
	 * Often needed by different packages Return a time string in form of hh:mm.
	 */
	protected static String makeTimeString(long time) {
		Date date = new Date(time);
		return itoa(date.hour) + ":" + itoa(date.min);
	}

	/**
	 * Converts an integer number in a String with given number of chars; fills
	 * in zeros if needed from the left side. Example: itoa(23, 4) -&gt; 0023.
	 */
	public static String itoa(int i, int limit) {
		String a = "" + i;
		if (a.length() < limit)
			a = "0" + a;
		return a;
	}

	/**
	 * Calls itoa(i,2);
	 */
	public static String itoa(int i) {
		return itoa(i, 2);
	}

	protected static String makeSizeString(int size) {
		String bytes = "B";
		if (size > 1024) {
			size /= 1024;
			bytes = "kB";
		}

		if (size > 1024) {
			size /= 1024;
			bytes = "Mb";
		}

		if (size > 1024) {
			size /= 1024;
			bytes = "Gb";
		}

		return "" + size + " " + bytes;
	}


	protected String throwableToStrackTrace(Throwable t) {
		StringWriter s = new StringWriter();
		PrintWriter p = new PrintWriter(s);
		t.printStackTrace(p);
		return s.toString();
	}
}