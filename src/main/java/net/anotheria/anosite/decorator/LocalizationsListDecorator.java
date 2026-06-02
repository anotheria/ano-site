package net.anotheria.anosite.decorator;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.asg.exception.ASGRuntimeException;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Decorator for linked scripts.
 *
 * @author denis
 */
public class LocalizationsListDecorator extends LinksListDecorator {

	/**
	 * As web data service for MediaLinks retrieval.
	 */
	private static IASResourceDataService service;

	/**
	 * Init.
	 */
	static {
		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(LocalizationsListDecorator.class).error(MarkerFactory.getMarker("FATAL"), "IASSiteDataService  init failure", e);
		}
	}

	@Override
	protected String getLinkTargetName(String targetId) throws ASGRuntimeException {
		return service.getLocalizationBundle(targetId).getName();
	}


}
