package net.anotheria.anosite.content.variables;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.localization.LocalizationMap;
import net.anotheria.anosite.tags.content.BaseResourceTag;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Variables processor for box translations.
 *
 * @author rpotopa
 */
public class LocalizationProcessor implements VariablesProcessor {
	/**
	 * {@link Logger} instance for view used keys.
	 */
	private static final Logger TEXT_RESOURCE_LOGGER = LoggerFactory.getLogger("TextResourceLog");

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseResourceTag.class);

	/**
	 * Prefix.
	 */
	public static final String PREFIX = DefinitionPrefixes.PREFIX_LOCALIZATION_MESSAGE;
	
	/**
	 * Resource data service.
	 */
	private static IASResourceDataService service;

	/**
	 * Init.
	 */
	static {
		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "IASResourceDataService init failure", e);
		}
	}


	@Override
	public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
		if (PREFIX.equals(prefix) && !StringUtils.isEmpty(variable)) {
			TEXT_RESOURCE_LOGGER.info(variable);
			String value = getLocalizationFromMap(variable);
			if(value != null)
				return value;
			value = getLocalizationFromResources(variable);
			return value != null ? value : "Missing translation: " + variable;
		}
		return defValue;
	}
	
	protected String getLocalizationFromMap(String key){
		LocalizationMap localization = (LocalizationMap)APICallContext.getCallContext().getAttribute(LocalizationMap.CALL_CONTEXT_SCOPE_NAME);
		return localization.getMessage(key);
	}
	
	/**
	 * Returns a text resource by its name.
	 * @param key  TODO dummy comment for javadoc.
	 * @return  TODO dummy comment for javadoc.
	 */
	protected String getLocalizationFromResources(String key){
		try{
			List<TextResource> resources = service.getTextResourcesByProperty(TextResource.PROP_NAME, key);
			if (resources==null || resources.size()==0)
				return null;
			return resources.get(0).getValue();
		}catch(ASGRuntimeException e){
			LOGGER.error("getLocalizationFromResources(" + key + ")", e);
			return null;
		}
	}
}
