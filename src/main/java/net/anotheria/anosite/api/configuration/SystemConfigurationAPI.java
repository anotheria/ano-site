package net.anotheria.anosite.api.configuration;

import net.anotheria.anoplass.api.API;

import java.util.List;

/**
 * System Configuration API.
 *
 */
public interface SystemConfigurationAPI extends API {

	String getCurrentSystem();

	String getCurrentSystemExpanded();

	List<String> getAvailableSystems();

	String getCurrentApplication();
}
