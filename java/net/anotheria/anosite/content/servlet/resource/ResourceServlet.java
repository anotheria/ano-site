package net.anotheria.anosite.content.servlet.resource;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.content.servlet.resource.type.ResourceReadType;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.shared.ResourceServletMappingConfig;
import net.anotheria.moskito.web.MoskitoHttpServlet;
import net.anotheria.util.StringUtils;
import net.anotheria.webutils.filehandling.actions.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Resource servlet. Serves for Images and Files stream.
 *
 * @author h3ll
 */
public abstract class ResourceServlet extends MoskitoHttpServlet {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ResourceServlet.class);

	/**
	 * ResourceServletMappingConfig instance.
	 */
	private static final ResourceServletMappingConfig config = ResourceServletMappingConfig.getInstance();

	/**
	 * IASResourceDataService instance.
	 */
	private IASResourceDataService resourceService;


	/**
	 * Constructor.
	 */
	public ResourceServlet() {
		try {
			resourceService = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LOG.error("ResourceServlet init failure", e);
			throw new RuntimeException("ResourceServlet init failure", e);
		}
	}

	@Override
	protected void moskitoDoGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String[] params = ResourceServletUtils.parsePathParameters(req);


		if (params == null || params.length == 0) {
			String message = "Illegal parameters!";
			LOG.info("moskitoDoGet(req, resp) fail. " + message);
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;

		}
		LOG.debug("Params[" + params + "].");

		if (params.length == 1) {
			if (StringUtils.isEmpty(params[0])) {
				String message = "Illegal parameters!";
				LOG.info("moskitoDoGet(req, resp) fail. " + message);
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			try {
				File file = FileStorage.getFile(params[0]);
				ResourceServletUtils.streamFile(res, file);
			} catch (IOException e) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND, "File " + params[0] + " not found! Probably deleted on disk. PATH = " + req.getPathInfo());
			}
			return;
		}

		if (params.length >= 2) {
			if (StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
				String message = "Illegal parameters!";
				LOG.info("moskitoDoGet(req, resp) fail. " + message);
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}


			ResourceReadType type = ResourceReadType.getByValue(params[0]);
			//finding  fileName! For  BY_DIRECT_FILE_NAME we should not load any cms  document!!!! Simply  taka params[1] as fileName
			String fileName = type != ResourceReadType.BY_DIRECT_FILE_NAME ? getFileName(type, params[1]) : params[1];

			if (StringUtils.isEmpty(fileName)) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND, "File " + type.getValue() + "[" + params[1] + "] not found. PATH = " + req
						.getPathInfo());
				return;
			}

			try {
				File file = FileStorage.getFile(fileName);
				ResourceServletUtils.streamFile(res, file);
			} catch (IOException e) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + fileName + " not found! Probably deleted on disk. PATH = " + req.getPathInfo());
			}
		}

	}


	/**
	 * Returns file name, from document which was selected!
	 *
	 * @param type		  {@link ResourceReadType}
	 * @param propertyValue value of property
	 * @return file name
	 * @throws jakarta.servlet.ServletException on errors from ResourceService
	 */

	protected abstract String getFileName(ResourceReadType type, String propertyValue) throws ServletException;


	public static Logger getLog() {
		return LOG;
	}

	public IASResourceDataService getResourceService() {
		return resourceService;
	}

	@Override
	protected void moskitoDoPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
	}


}
