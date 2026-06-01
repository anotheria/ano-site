package net.anotheria.anosite.config;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Delivery mode for static content like JS/CSS.
 * 
 * @author Alexandr Bolbat
 */
@ConfigureMe(name = "ano-site-resource-delivery-config")
public final class ResourceDeliveryConfig implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	@DontConfigure
	private static final long serialVersionUID = -366101733489839363L;

	/**
	 * Logger.
	 */
	@DontConfigure
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDeliveryConfig.class.getName());

	/**
	 * Configuration instance.
	 */
	@DontConfigure
	private static ResourceDeliveryConfig INSTANCE;

	/**
	 * Is enabled for resources defined in CMS (CSS/JS).
	 */
	@Configure
	private boolean useForCMSEnabled = false;

	/**
	 * Servlet mapping defined in web.xml.
	 */
	@Configure
	private String servletMapping = "rd";

	/**
	 * Is need use application version in URL's to resources.
	 */
	@Configure
	private boolean useAppVersionInURL = false;

	/**
	 * Version prefix.
	 */
	@Configure
	private String versionPrefix = "V-";

	/**
	 * Set HTTP header's to never expire for resources.
	 */
	@Configure
	private boolean resourceNeverExpire = false;

	/**
	 * Get instance method.
	 * 
	 * @return {@link ResourceDeliveryConfig}
	 */
	public static synchronized ResourceDeliveryConfig getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ResourceDeliveryConfig();

		return INSTANCE;
	}

	/**
	 * Default constructor.
	 */
	private ResourceDeliveryConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
			LOGGER.info("ResourceDeliveryConfig() Configured. Configuration[" + this.toString() + "].");
		} catch (Exception e) {
			LOGGER.warn("ResourceDeliveryConfig() Configuration failed. Configuring with defaults[" + this.toString() + "].");
		}
	}

	public boolean isUseForCMSEnabled() {
		return useForCMSEnabled;
	}

	public void setUseForCMSEnabled(boolean aUseForCMSEnabled) {
		this.useForCMSEnabled = aUseForCMSEnabled;
	}

	public String getServletMapping() {
		return servletMapping;
	}

	public void setServletMapping(String aServletMapping) {
		this.servletMapping = aServletMapping;
	}

	public boolean isUseAppVersionInURL() {
		return useAppVersionInURL;
	}

	public void setUseAppVersionInURL(final boolean aUseAppVersionInURL) {
		this.useAppVersionInURL = aUseAppVersionInURL;
	}

	public boolean isResourceNeverExpire() {
		return resourceNeverExpire;
	}

	public void setResourceNeverExpire(final boolean aResourceNeverExpire) {
		this.resourceNeverExpire = aResourceNeverExpire;
	}

	public String getVersionPrefix() {
		return versionPrefix;
	}

	public void setVersionPrefix(final String aVersionPrefix) {
		this.versionPrefix = aVersionPrefix;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResourceDeliveryConfig [");
		builder.append("useForCMSEnabled=" + useForCMSEnabled);
		builder.append(", servletMapping=" + servletMapping);
		builder.append(", useAppVersionInURL=" + useAppVersionInURL);
		builder.append(", versionPrefix=" + versionPrefix);
		builder.append(", resourceNeverExpire=" + resourceNeverExpire);
		builder.append("]");
		return builder.toString();
	}

}
