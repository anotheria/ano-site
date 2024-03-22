package net.anotheria.anosite.content.servlet;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.util.StringUtils;
import net.anotheria.webutils.filehandling.servlet.FileDeliveryServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Servlet that delivers file by alias.
 *
 * @author dsilenko
 */
public class AliasFileDeliveryServlet extends FileDeliveryServlet {

	/**
	 * Logger instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AliasFileDeliveryServlet.class);

	/**
	 * Generated serialVersionUID.
	 */
	private static final long serialVersionUID = -9048929437969384125L;

	/**
	 * Instance of IASResourceDataService.
	 */
	private IASResourceDataService service;

	public AliasFileDeliveryServlet() {
		super();

		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error("AliasFileDeliveryServlet()", e);
			throw new RuntimeException("AliasFileDeliveryServlet()", e);
		}
	}


	@Override
	protected String getRequestedResourceName(HttpServletRequest req) throws ServletException {
		String name = super.getRequestedResourceName(req);
		if (StringUtils.isEmpty(name))
			return null;

		try {

			for (Image image : service.getImages()) {
				if (image.getAlias().equals(name))
					return image.getImage();
			}

		} catch (ASResourceDataServiceException e) {
			LOGGER.error("getRequestedResourceName()", e);
			throw new ServletException("getRequestedResourceName()", e);
		}

		return null;
	}

}
