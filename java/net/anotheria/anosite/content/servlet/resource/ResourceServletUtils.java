package net.anotheria.anosite.content.servlet.resource;

import net.anotheria.util.IOUtils;
import net.anotheria.webutils.filehandling.beans.TemporaryFileHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
	 * @param fileHolder	 {@link java.io.File} to stream
	 * @throws java.io.IOException on IO errors
	 */
	protected static void streamFile(HttpServletResponse response, TemporaryFileHolder fileHolder) throws IOException {

		response.setContentType(fileHolder.getMimeType());
		response.setContentLength(fileHolder.getData().length);
		response.setHeader(LAST_MODIFIED_HEADER, new Date(fileHolder.getLastModified()).toString());
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(fileHolder.getData());
			org.apache.commons.io.IOUtils.copyLarge(in, response.getOutputStream());
		} catch (IOException e) {
			LOG.error("streamFile[response, {}] failed", fileHolder.getFileName(), e);
			throw e;
		} finally {
			IOUtils.closeIgnoringException(in);
		}
	}

	private ResourceServletUtils() {
		throw new IllegalAccessError("Can't be instantiated");
	}
}
