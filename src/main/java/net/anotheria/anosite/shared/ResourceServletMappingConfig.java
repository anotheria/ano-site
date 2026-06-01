package net.anotheria.anosite.shared;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.slf4j.LoggerFactory;

/**
 * ResourceServletMappingConfig.
 *
 * @author h3ll
 */
@ConfigureMe(name = "resource-servlet-mapping")
public class ResourceServletMappingConfig {
	/**
	 * Separator.
	 */
	private static final String SEPARATOR = "/";
	/**
	 * Default value for Image servlet mapping.
	 */
	private static final String DEFAULT_IMAGE_SERVLET_MAPPING = "/cmsImage/";
	/**
	 * Default value for File servlet mapping.
	 */
	private static final String DEFAULT_FILE_SERVLET_MAPPING = "/cmsFile/";

	/**
	 * ResourceServletMappingConfig instance.
	 */
	private static ResourceServletMappingConfig instance;

	/**
	 * ResourceServletMappingConfig servlet mapping for image servlet.
	 */
	@Configure
	private String imageServletMapping;
	/**
	 * ResourceServletMappingConfig servlet mapping for file servlet.
	 */
	@Configure
	private String fileServletMapping;

	/**
	 * Get instance method.
	 *
	 * @return {@link  ResourceServletMappingConfig}
	 */
	public static synchronized ResourceServletMappingConfig getInstance() {
		if (instance == null) {
			instance = new ResourceServletMappingConfig();
			try {
				ConfigurationManager.INSTANCE.configure(instance);
			} catch (Exception e) {
				LoggerFactory.getLogger(ResourceServletMappingConfig.class).warn("Configuration FAILED. Relying on defaults.", e);
			}
		}
		return instance;
	}


	/**
	 * Constructor.
	 */
	private ResourceServletMappingConfig() {
		imageServletMapping = DEFAULT_IMAGE_SERVLET_MAPPING;
		fileServletMapping = DEFAULT_FILE_SERVLET_MAPPING;
	}

	public String getImageServletMapping() {
		return imageServletMapping;
	}

	/**
	 * This setter will quote incoming value with "/" if required!
	 *
	 * @param imageServletMapping value
	 */
	public void setImageServletMapping(String imageServletMapping) {
		imageServletMapping = imageServletMapping.startsWith(SEPARATOR) ? imageServletMapping : SEPARATOR + imageServletMapping;
		imageServletMapping = imageServletMapping.endsWith(SEPARATOR) ? imageServletMapping : imageServletMapping + SEPARATOR;
		this.imageServletMapping = imageServletMapping;
	}

	public String getFileServletMapping() {
		return fileServletMapping;
	}

	/**
	 * This setter will quote incoming value with "/" if required!
	 *
	 * @param fileServletMapping value
	 */
	public void setFileServletMapping(String fileServletMapping) {
		fileServletMapping = fileServletMapping.startsWith(SEPARATOR) ? fileServletMapping : SEPARATOR + fileServletMapping;
		fileServletMapping = fileServletMapping.endsWith(SEPARATOR) ? fileServletMapping : fileServletMapping + SEPARATOR;
		this.fileServletMapping = fileServletMapping;
	}

	@Override
	public String toString() {
		return "ResourceServletMappingConfig{" +
				"imageServletMapping='" + imageServletMapping +
				", fileServletMapping='" + fileServletMapping +
				'}';
	}
}
