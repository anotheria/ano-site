package net.anotheria.anosite.shared.presentation.servlet;

import net.anotheria.moskito.web.MoskitoHttpServlet;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Base servlet class for anosite servlets.
 * @author another
 *
 */
public abstract class BaseAnoSiteServlet extends MoskitoHttpServlet {
	/**
	 * SerialVersion UID.
	 */
	private static final long serialVersionUID = 1L;

	protected static String extractArtifactName(HttpServletRequest req) {
		String servletPath = req.getServletPath();
		if (servletPath.length() > 0 && servletPath.charAt(0) == '/')
			servletPath = servletPath.substring(1);
		int indexOfDot = servletPath.indexOf('.');
		if (indexOfDot != -1)
			servletPath = servletPath.substring(0, indexOfDot);
		return servletPath;
	}

	@Override
	public String getProducerId() {
		return getClass().getSimpleName();
	}
}
