package net.anotheria.anosite.content.servlet.resource;

import net.anotheria.util.IOUtils;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Support utilities for ResourceServlet.
 *
 * @author h3ll
 */
final class ResourceServletUtils {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ResourceServletUtils.class);

	/**
	 * Header LAST_MODIFIED.
	 */
	private static final String LAST_MODIFIED_HEADER = "Last-Modified";

	/**
	 * Mime types map.
	 */
	private static Map<String, String> extensionMapping;

	/**
	 * Extension init.
	 */
	static {
		extensionMapping = new HashMap<String, String>();
		extensionMapping.put("pdf", "application/pdf");
		extensionMapping.put("jpe", "image/jpeg");
		extensionMapping.put("jpeg", "image/jpeg");
		extensionMapping.put("jpg", "image/jpeg");
		extensionMapping.put("png", "image/png");
		extensionMapping.put("js", "text/javascript");
		extensionMapping.put("exe", "application/octet-stream");
		extensionMapping.put("gif", "image/gif");
		extensionMapping.put("gz", "application/x-gzip");
		extensionMapping.put("zip", "application/zip");
		extensionMapping.put("doc", "application/msword");
		extensionMapping.put("dot", "application/msword");
		extensionMapping.put("pot", "application/vnd.ms-powerpoint");
		extensionMapping.put("ppt", "application/vnd.ms-powerpoint");
		extensionMapping.put("pps", "application/vnd.ms-powerpoint");
	}

	/**
	 * Splits the REST-like path of the request URI into tokens.
	 *
	 * @param request {@link jakarta.servlet.http.HttpServletRequest}
	 * @return parameter string splitted
	 */
	protected static String[] parsePathParameters(HttpServletRequest request) {
		return request.getPathInfo().substring(1).split("/");
	}

	/**
	 * Streams some file to the response.
	 *
	 * @param response {@link jakarta.servlet.http.HttpServletResponse}
	 * @param file	 {@link java.io.File} to stream
	 * @throws java.io.IOException on IO errors
	 */
	protected static void streamFile(HttpServletResponse response, File file) throws IOException {

		response.setContentType(guessMimeType(file.getName()));
		response.setContentLength((int) file.length());
		response.setHeader(LAST_MODIFIED_HEADER, new Date(file.lastModified()).toGMTString());
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			org.apache.commons.io.IOUtils.copyLarge(in, response.getOutputStream());
		} catch (IOException e) {
			LOG.error("streamFile[response, " + file + "] failed", e);
			throw e;
		} finally {
			IOUtils.closeIgnoringException(in);
		}
	}


	/**
	 * Returns mime type - according to file extension.
	 *
	 * @param fName name of file
	 * @return mime type if available
	 */
	private static String guessMimeType(String fName) {
		if (!StringUtils.isEmpty(fName)) {
			String extension = fName.contains(".") && fName.lastIndexOf(".") != fName.length() - 1 ? fName.substring(fName.lastIndexOf('.') + 1) : null;
			if (extension != null)
				return extensionMapping.get(extension);
		}
		return null;
	}

	private ResourceServletUtils() {
		throw new IllegalAccessError("Can't be instantiated");
	}
}
