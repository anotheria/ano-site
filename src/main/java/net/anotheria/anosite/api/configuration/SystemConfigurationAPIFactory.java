package net.anotheria.anosite.api.configuration;

import net.anotheria.anoplass.api.APIFactory;

/**
 * Factory for {@link SystemConfigurationAPI}
 *
 */
public class SystemConfigurationAPIFactory implements APIFactory<SystemConfigurationAPI> {
	@Override
	public SystemConfigurationAPI createAPI() {
		return new SystemConfigurationAPIImpl();
	}
}
