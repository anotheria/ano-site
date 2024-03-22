package net.anotheria.anosite.content.variables;

import net.anotheria.anosite.shared.ResourceServletMappingConfig;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Basic processor for links to external resources like files or images.
 *
 * @author lrosenberg
 */
public abstract class XLinkProcessor implements VariablesProcessor {
	/**
	 * URL separator.
	 */
	protected static final String SEPARATOR = "/";
	/**
	 * Logger.
	 */
	private Logger log;

	/**
	 * {@link ResourceServletMappingConfig} instance.
	 */
	private ResourceServletMappingConfig resourceServletMappingConfiguration;

	/**
	 * Constructor.
	 */
	public XLinkProcessor() {
		log = LoggerFactory.getLogger(this.getClass());
		resourceServletMappingConfiguration = ResourceServletMappingConfig.getInstance();
	}

	protected Logger getLog() {
		return log;
	}

	public ResourceServletMappingConfig getResourceServletMappingConfiguration() {
		return resourceServletMappingConfiguration;
	}

	@Override
	public String replace(final String prefix, final String variable, final String defValue, final HttpServletRequest req) {
		if (StringUtils.isEmpty(variable)) {
			log.debug("Illegal incoming var!");
			return null;
		}

		String fileName = getFileName(variable);
		if (StringUtils.isEmpty(fileName))
			return null;

		return getResourcePath(req.getContextPath()) + fileName;
	}


	/**
	 * Returns URL part with context path and mapping to proper servlet.
	 * Path will contains all required parts - for resource reach via
	 * {@link net.anotheria.anosite.content.servlet.resource.ResourceServlet}.
	 *
	 * @param contextPath request context path
	 * @return string
	 */
	protected abstract String getResourcePath(String contextPath);

	/**
	 * Returns the filename corresponding with the variable.
	 *
	 * @param variable var itself
	 * @return file name
	 */
	protected abstract String getFileName(String variable);

}
