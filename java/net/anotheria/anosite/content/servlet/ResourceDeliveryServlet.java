package net.anotheria.anosite.content.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.anotheria.anosite.config.ResourceDeliveryConfig;
import net.anotheria.util.log.LogMessageUtil;
import net.java.dev.moskito.web.MoskitoHttpServlet;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Servlet for delivering static resources.
 * 
 * @author Alexandr Bolbat
 */
public class ResourceDeliveryServlet extends MoskitoHttpServlet {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -8935110768011959639L;

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOG = Logger.getLogger(ResourceDeliveryServlet.class);

	/**
	 * {@link ResourceDeliveryConfig} instance.
	 */
	private static ResourceDeliveryConfig config = ResourceDeliveryConfig.getInstance();

	/**
	 * US locale - all HTTP dates are in English.
	 */
	public final static Locale LOCALE_US = Locale.US;

	/**
	 * GMT time zone - all HTTP dates are on GMT.
	 */
	public final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

	/**
	 * Format for RFC 1123 date string.
	 */
	public final static String RFC1123_PATTERN = "EEE, dd MMM yyyyy HH:mm:ss z";

	@Override
	protected void moskitoDoGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		String path = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;

		if (path.startsWith(config.getVersionPrefix())) {
			int idx = path.indexOf("/");
			if (idx == -1) {
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			path = path.substring(idx + 1);
		}

		ContentType type = null;
		if (path.toLowerCase().endsWith(".css")) // CSS resource
			type = ContentType.CSS;
		if (path.toLowerCase().endsWith(".js"))
			type = ContentType.JS; // JS resource
		if (path.toLowerCase().endsWith(".jpg"))
			type = ContentType.JPG; // image resource
		if (path.toLowerCase().endsWith(".jpeg"))
			type = ContentType.JPEG; // image resource
		if (path.toLowerCase().endsWith(".png"))
			type = ContentType.PNG; // image resource
		if (path.toLowerCase().endsWith(".gif"))
			type = ContentType.GIF; // image resource

		if (type == null) { // content type must be taken from extension or this is HttpServletResponse.SC_BAD_REQUEST
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		File resource = new File(getServletContext().getRealPath("") + File.separator + path);
		if (!resource.exists()) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, pathInfo);
			return;
		}

		// setting right header's
		setHeaders(res, type, resource.lastModified());

		// streaming resource
		stream(resource, res);
	}

	/**
	 * Stream resource if exist.
	 * 
	 * @param path
	 *            - resource file path
	 * @param resp
	 *            - response
	 * @return <code>true</code> if resource steamed or <code>false</code>
	 */
	private void stream(final File resourceFile, final HttpServletResponse res) {
		InputStream in = null;
		try {
			in = new FileInputStream(resourceFile);
			OutputStream out = res.getOutputStream();
			IOUtils.copyLarge(in, out);
			out.flush();
		} catch (IOException e) {
			String message = LogMessageUtil.failMsg(e, resourceFile, res);
			LOG.warn(message, e);
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Set HTTP header's.
	 * 
	 * @param res
	 *            - response
	 * @param contentType
	 *            - content type
	 * @param lastModifiedDate
	 *            - resource last modification date
	 */
	private void setHeaders(final HttpServletResponse res, final ContentType contentType, final long lastModifiedDate) {
		// setting content type
		res.setContentType(contentType.getType());

		DateFormat df = new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);
		df.setTimeZone(GMT_ZONE);

		// Setting last modified header
		res.setHeader("Last-Modified", df.format(new Date(lastModifiedDate)));

		// Setting expiration header
		Calendar cal = Calendar.getInstance();

		if (config.isResourceNeverExpire()) // set resource expiration time to 1 year
			cal.add(Calendar.YEAR, 1);
		else
			cal.add(Calendar.DAY_OF_YEAR, 3); // set resource expiration time to 3 day's

		res.addHeader("Expires", df.format(cal.getTime()));
	}

	/**
	 * Content type.
	 * 
	 * @author Alexandr Bolbat
	 */
	public enum ContentType {

		/**
		 * CSS type.
		 */
		CSS("text/css"),

		/**
		 * JavaScript type.
		 */
		JS("text/javascript"),

		/**
		 * PNG image file.
		 */
		PNG("image/png"),

		/**
		 * JPG image file.
		 */
		JPG("image/jpg"),

		/**
		 * JPEG image file.
		 */
		JPEG("image/jpeg"),

		/**
		 * GIF image file.
		 */
		GIF("image/gif");

		/**
		 * String representation for use in HTML page or HTTP header.
		 */
		private final String type;

		/**
		 * Private constructor.
		 * 
		 * @param aType
		 *            - string representation
		 */
		private ContentType(final String aType) {
			this.type = aType;
		}

		public String getType() {
			return type;
		}

	}
}
