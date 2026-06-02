package net.anotheria.anosite.tags.content;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.tags.BaseTagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.jsp.JspException;
import java.util.List;

/**
 * Base tag for handling resources in the jsp.
 * @author lrosenberg
 *
 */
public abstract class BaseResourceTag extends BaseTagSupport {
	/**
	 * Resource data service.
	 */
	private static IASResourceDataService service;
	/**
	 * log.
	 */
	private static Logger log = LoggerFactory.getLogger(BaseResourceTag.class);

	/**
	 * Init.
	 */
	static {
		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			log.error(MarkerFactory.getMarker("FATAL"), "IASResourceDataService init failure", e);
		}
	}

	protected static IASResourceDataService getResourceDataService(){
		return service;
	}
	/**
	 * Returns a text resource by its name.
	 * @param key TODO dummy comment for javadoc.
	 * @return TODO dummy comment for javadoc.
	 * @throws JspException TODO dummy comment for javadoc.
	 */
	protected TextResource getTextResourceByName(String key) throws JspException{
		try{
			List<TextResource> resources = getResourceDataService().getTextResourcesByProperty(TextResource.PROP_NAME, key);
			if (resources==null || resources.size()==0)
				return null;
			return resources.get(0);
		}catch(ASGRuntimeException e){
			log.error("getTextResourceByName("+key+")", e);
			throw new JspException(e);
		}
	}
}