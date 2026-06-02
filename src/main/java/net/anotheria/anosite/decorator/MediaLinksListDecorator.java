package net.anotheria.anosite.decorator;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.assitedata.service.IASSiteDataService;
import net.anotheria.asg.exception.ASGRuntimeException;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Decorator for linked localizations.
 * @author denis
 */
public class MediaLinksListDecorator extends LinksListDecorator{
	
	/**
	 * As web data service for LocalizationBundles retrieval.
	 */
	private static IASSiteDataService service;

	/**
	 * Init.
	 */
	static {
		try {
			service = MetaFactory.get(IASSiteDataService.class);
		} catch (MetaFactoryException e) {
          LoggerFactory.getLogger(MediaLinksListDecorator.class).error(MarkerFactory.getMarker("FATAL"), "IASSiteDataService  init failure", e);
		}
	}

	@Override
	protected String getLinkTargetName(String targetId) throws ASGRuntimeException {
		return service.getMediaLink(targetId).getName();
	}
	

}
