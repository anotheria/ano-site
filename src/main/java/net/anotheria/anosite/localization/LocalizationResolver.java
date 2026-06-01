package net.anotheria.anosite.localization;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.asg.exception.ASGRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.List;


/**
 * Not real resolver but some workaround as localizations adder from
 * LocalizationMap and TextResources.
 * Will be rewritten with new Localization Framework.
 * 
 * @author denis
 */

public enum LocalizationResolver {

	INSTANCE;

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationResolver.class);

	private static IASResourceDataService resourceService;

	static {
		try {
			resourceService = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "IASResourceDataService init failure", e);
		}
	}

	public String getLocalizedMessage(String key) {
		// Try to find resource with given key in LocalizationMap
		
		String message = LocalizationMap.getCurrentLocalizationMap().getMessage(key);
		if (message != null)
			return message;

		// Ok, resource in LocalizationMap not found. Loading from TextResources
		TextResource resource = getTextResourceByName(key);
		if (resource != null)
			message = resource.getValue();
		return message != null? message: "Missing key: " + key;
	}

	/**
	 * Returns a text resource by its name.
	 * 
	 * @param key - name of the resource.
	 * @return TextResource.
	 * @throws RuntimeException
	 *             on ResourceService failure
	 */
	protected TextResource getTextResourceByName(String key) throws RuntimeException {
		try {
			List<TextResource> resources = resourceService.getTextResourcesByProperty(TextResource.PROP_NAME, key);
			if (resources == null || resources.size() == 0)
				return null;
			return resources.get(0);
		} catch (ASGRuntimeException e) {
			LOGGER.error("Error while loading localizations from ResourceDataService: ", e);
			throw new RuntimeException("Error while loading localizations from ResourceDataService: ", e);
		}
	}
}
