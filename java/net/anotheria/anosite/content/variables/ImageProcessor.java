package net.anotheria.anosite.content.variables;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.shared.ResourceServletMappingConfig;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Image processor.
 */
public class ImageProcessor implements VariablesProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ImageProcessor.class);
	/**
	 * {@link IASResourceDataService} instance.
	 */
	private static IASResourceDataService service;
	/**
	 * {@link ResourceServletMappingConfig} instance.
	 */
	private static ResourceServletMappingConfig mappingConfig = ResourceServletMappingConfig.getInstance();
	/**
	 * URL separator.
	 */
	private static final String SEPARATOR_STRING = "/";

	/**
	 * Static initialization block.
	 */
	static {
		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(ImageLinkProcessor.class).error(MarkerFactory.getMarker("FATAL"), "Not properly initialized, can't find resource data service.");
		}
	}

	@Override
	public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
		if (StringUtils.isEmpty(variable)) {
			LOG.debug("Invalid incoming parameter! Variable!");
			return null;
		}

		try {
			List<Image> imgList = service.getImagesByProperty(Image.PROP_NAME, variable);
			if (imgList == null || imgList.isEmpty()) {
				LOG.debug("Image with name " + variable + " not found!");
				return null;
			}
			Image img = imgList.get(0);
			if (img == null) {
				return null;
			}

			String filePath = mappingConfig.getImageServletMapping() + img.getImage();
			if (!req.getContextPath().isEmpty())
				filePath = req.getContextPath() + filePath;

			String alt = img.getAlt();
			String title = img.getTitle();

			if(alt.isEmpty()){
				return "<img src=\"" + filePath + "\" alt=\"" + title + "\" border=\"0\"/>";
			}
			else {
				return "<img src=\"" + filePath + "\" alt=\"" + alt + "\" border=\"0\"/>";
			}
		} catch (ASResourceDataServiceException e) {
			LOG.error("ASResourceDataServiceException failed", e);
			return null;
		}
	}


}
