package net.anotheria.anosite.shared.presentation.listener;

import net.anotheria.anodoc.service.LockHolder;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.api.configuration.SystemConfigurationAPI;
import net.anotheria.anosite.api.configuration.SystemConfigurationAPIFactory;
import net.anotheria.anosite.api.feature.FeatureAPI;
import net.anotheria.anosite.api.feature.FeatureAPIFactory;
import net.anotheria.anosite.cms.helper.BoxHelperUtility;
import net.anotheria.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;



/**
 * This listener performs the webapp initialization upon webserver start (or webapp hot-re-deploy).
 * @author lrosenberg
 */
public class ContextInitializer implements ServletContextListener{

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ContextInitializer.class);

	public void contextDestroyed(ServletContextEvent event) {
		LOGGER.info("CONTEXT DESTROYED @ " + Date.currentDate());
		
	}

	public void contextInitialized(ServletContextEvent event) {
		
		String myName = event.getServletContext().getContextPath()+" context ";
		
		LOGGER.info(myName + " CONTEXT INITIALIZED @ " + Date.currentDate());
		CMSSelfTest.performSelfTest();
		BoxHelperUtility.setup();
		
		//configure API!
		LOGGER.info(myName + "Configure api");
		APIFinder.addAPIFactory(FeatureAPI.class, new FeatureAPIFactory());
		APIFinder.addAPIFactory(SystemConfigurationAPI.class, new SystemConfigurationAPIFactory());
		LOGGER.info(myName + "API configured");
		LockHolder.addShutdownHook();
		LOGGER.info(myName + "added shutdown hook");

	}
	
}
