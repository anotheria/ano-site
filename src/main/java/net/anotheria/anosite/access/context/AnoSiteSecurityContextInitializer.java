package net.anotheria.anosite.access.context;

import java.util.HashMap;
import java.util.Map;

import net.anotheria.anoplass.api.APICallContext;

/**
 * Ano-site main security context initializer.
 * 
 * @author Alexandr Bolbat
 */
public class AnoSiteSecurityContextInitializer implements SecurityContextInitializer {

	/**
	 * Property name for current user id.
	 */
	public static final String PROPERTY_CURRENT_USER_ID = "current_user_id";

	/**
	 * Property name for current user agent.
	 */
	public static final String PROPERTY_CURRENT_USER_AGENT = "current_user_agent";

	/**
	 * Property name for current locale.
	 */
	public static final String PROPERTY_CURRENT_LOCALE = "current_locale";

	@Override
	public Map<String, String> initialize() {
		Map<String, String> result = new HashMap<String, String>();

		result.put(PROPERTY_CURRENT_USER_ID, APICallContext.getCallContext().getCurrentUserId());
		result.put(PROPERTY_CURRENT_USER_ID, APICallContext.getCallContext().getCurrentSession().getUserAgent());
		result.put(PROPERTY_CURRENT_LOCALE, APICallContext.getCallContext().getCurrentLocale().toString());

		return result;
	}

}
