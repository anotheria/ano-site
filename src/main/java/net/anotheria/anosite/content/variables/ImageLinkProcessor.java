package net.anotheria.anosite.content.variables;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.List;

/**
 * Processor for images uploaded via IASResourceDataService.
 *
 * @author lrosenberg
 */
public class ImageLinkProcessor extends XLinkProcessor {
	private static IASResourceDataService service;

	static {
		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(ImageLinkProcessor.class).error(MarkerFactory.getMarker("FATAL"), "Not properly initialized, can't find resource data service.");
		}

	}

	@Override
	protected String getResourcePath(String contextPath) {
		String result = getResourceServletMappingConfiguration().getImageServletMapping();
		if (!StringUtils.isEmpty(contextPath))
			result = contextPath + result;
		return result;
	}

	@Override
	protected String getFileName(String variable) {
		try {
			Image image;
			List<Image> images = service.getImagesByProperty(Image.PROP_NAME, variable);
			if (images == null || images.isEmpty()) {
				getLog().debug("Image with name [" + variable + "] not found");
				return null;
			}
			image = images.get(0);

			if (image == null) {
				getLog().debug("Image with name [" + variable + "] found! But  due to some strange reasons is NULL");
				return null;
			}

			if (!StringUtils.isEmpty(image.getImage()))
				return image.getImage();

			throw new RuntimeException("CMS resource Image name[" + variable + "] property file is miss-configured! Check Resource please!" + image);
		} catch (Exception e) {
			getLog().warn("getFileName(" + variable + ") failure: ", e);
			return null;
		}
	}
}
