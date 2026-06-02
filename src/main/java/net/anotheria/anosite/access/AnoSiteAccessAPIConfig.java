package net.anotheria.anosite.access;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@ConfigureMe(name = "ano-site-access-api-config")
public final class AnoSiteAccessAPIConfig implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	@DontConfigure
	private static final long serialVersionUID = 6091949471599059300L;

	/**
	 * {@link Logger} instance.
	 */
	@DontConfigure
	private static final Logger LOGGER = LoggerFactory.getLogger(AnoSiteAccessAPIConfig.class.getName());

	/**
	 * {@link AnoSiteAccessAPIConfig} instance.
	 */
	@DontConfigure
	private static final AnoSiteAccessAPIConfig INSTANCE = new AnoSiteAccessAPIConfig();

	@Configure
	private boolean enabled;

	private AnoSiteAccessAPIConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
			LOGGER.info("AccessAPIConfig() Configured. Configuration[" + this.toString() + "].");
		} catch (Exception e) {
			this.enabled = false;
			LOGGER.warn("AccessAPIConfig() Configuration failed. Configuring with defaults[" + this.toString() + "].");
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public static AnoSiteAccessAPIConfig getInstance() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "AccessAPIConfig [enabled=" + enabled + "]";
	}

}
