package net.anotheria.anosite.content.variables.helper;

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
 * Helper class for TextResourceProcessors.
 *
 * @author h3llka
 */
public final class TextResourceProcessorHelper {

	/**
	 * Logger instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TextResourceProcessorHelper.class);

	/**
	 * IASResourceDataService instance.
	 */
	private static IASResourceDataService resourceDataService;

	/**
	 * Service init.
	 */
	static {
		try {
			resourceDataService = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "IASResourceDataService init failure", e);
			throw new RuntimeException("IASResourceDataService init failure", e);
		}
	}

	/**
	 * Prevent init constructor.
	 */
	private TextResourceProcessorHelper() {
	}

	/**
	 * Returns TextResource object, where name equals to incoming var.
	 *
	 * @param var variable - name
	 * @return TextResource object.
	 * @throws net.anotheria.asg.exception.ASGRuntimeException
	 *          on errors
	 */
	public static TextResource getTextResourceByName(String var) throws ASGRuntimeException {
		TextResource result = null;
		List<TextResource> resources = resourceDataService.getTextResourcesByProperty(TextResource.PROP_NAME, var);
		if (resources != null && resources.size() > 0) {
			result = resources.get(0);
		}
		return result;
	}
}
