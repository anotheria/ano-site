package net.anotheria.anosite.access.context;

import java.util.Map;

/**
 * General security context initializer interface.
 * 
 * @author Alexandr Bolbat
 */
public interface SecurityContextInitializer {

	/**
	 * Initialize required information for security context as key/value {@link Map}.
	 * 
	 * @return {@link Map}
	 */
	Map<String, String> initialize();

}
