package net.anotheria.anosite.localization;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.content.variables.VariablesUtility;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Storage for localization in different environment.
 * Will be rewritten with new Localization Framework.
 * 
 * @author denis
 */

public class LocalizationMap {
	/**
	 * Logger.
	 */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationMap.class);
	/**
	 * The name under which the localization map is stored in the call context
	 * to allow access by the variable processors.
	 */
	public static final String CALL_CONTEXT_SCOPE_NAME = LocalizationMap.class.getName();
	/**
	 * Current value of localization map.
	 */
	private Map<String, String> localizationBundles = new HashMap<>();
	/**
	 * {@link IASResourceDataService} instance.
	 */
	private IASResourceDataService resourceDataService;
	
	private LocalizationMap() {
		try {
			resourceDataService = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "Init IASResourceDataService failure", e);
			throw new RuntimeException("Init IASResourceDataService failure", e);
		}
	}

	private String getPrivateKey(LocalizationEnvironment environment, String key) {
		return environment.name() + "_" + key.trim();
	}

	public String getMessage(String key) {
		String message = localizationBundles.get(getPrivateKey(LocalizationEnvironment.BRAND, key));
		if (message == null)
			message = localizationBundles.get(getPrivateKey(LocalizationEnvironment.ACTION, key));
		if (message == null)
			message = localizationBundles.get(getPrivateKey(LocalizationEnvironment.BOX, key));

		if (message == null)
			message = localizationBundles.get(getPrivateKey(LocalizationEnvironment.PAGE, key));

		if (message == null)
			message = localizationBundles.get(getPrivateKey(LocalizationEnvironment.TEMPLATE, key));

		if (message == null) {
			message = localizationBundles.get(getPrivateKey(LocalizationEnvironment.SITE, key));
		}

		if (message == null)
			message = localizationBundles.get(getPrivateKey(LocalizationEnvironment.RESOURCES, key));

		message = VariablesUtility.replaceLocalizationBundleVariables(message);
		return message;
	}

	public void addLocalization(LocalizationEnvironment scope, Map<String, String> localization) {
		for (Entry<String, String> loc : localization.entrySet())
			localizationBundles.put(getPrivateKey(scope, loc.getKey()), loc.getValue());
	}

	public void addLocalizationBundles(LocalizationEnvironment scope, List<LocalizationBundle> bundles){
		for (LocalizationBundle bundle : bundles)
			addLocalizationBundle(scope, bundle);
	}

	public void addLocalizationBundle(LocalizationEnvironment scope, LocalizationBundle bundle){
		if (!StringUtils.isEmpty(bundle.getParentBundle())) {
			try {
				LocalizationBundle parentBundle = resourceDataService.getLocalizationBundle(bundle.getParentBundle());
				localizationBundles.putAll(parseBundle(scope, parentBundle));
			} catch (ASResourceDataServiceException e) {
				LOGGER.warn("Unable to get parent bundle. " + e.getMessage());
			}
		}
		localizationBundles.putAll(parseBundle(scope, bundle));
	}

	private Map<String, String> parseBundle(LocalizationEnvironment scope, LocalizationBundle bundle) {

		String toParse = bundle.getMessages();
		if(toParse == null){
			LOGGER.error("LocalizationBundle " + bundle.getId() + ": message is null!");
			return Collections.emptyMap();
		}

		Map<String, String> result = new HashMap<>();
		toParse = StringUtils.removeChar(toParse, '\r');
		String[] lines = StringUtils.tokenize(toParse, '\n');
		for (String l : lines) {
			if(StringUtils.isEmpty(l) || l.trim().startsWith("#"))
                continue;

            String escapedString = l.replace("\\=", "&#61;");
            if (!escapedString.contains("=")) {
                LOGGER.warn("Invalid format of LocalizationBundle with id " + bundle.getId() + " in line: <" + l + ">. Expected line format: <key=message>");
                continue;
            }
            String[] message = StringUtils.tokenize(escapedString, '=');
            int length = message.length;
            String key;
            String value;
            if ((length == 2 || length == 1) && !message[0].isEmpty()) {
                key = message[0].replace("&#61;", "=");
                value = length == 2 ? message[1].replace("&#61;", "=") : "";
            } else {
                LOGGER.warn("Invalid format of LocalizationBundle with id " + bundle.getId() + " in line: <" + l + ">. Expected line format: <key=message>");
                continue;
            }
            result.put(getPrivateKey(scope, key), value);
        }
		return result;
	}

	public static LocalizationMap getCurrentLocalizationMap() {
		LocalizationMap loc = (LocalizationMap) APICallContext.getCallContext().getAttribute(LocalizationMap.CALL_CONTEXT_SCOPE_NAME);
		if (loc != null)
			return loc;
		loc = new LocalizationMap();
		APICallContext.getCallContext().setAttribute(LocalizationMap.CALL_CONTEXT_SCOPE_NAME, loc);
		return loc;
	}
}
