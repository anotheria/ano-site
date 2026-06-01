package net.anotheria.anosite.handler;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.session.APISessionImpl;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.handler.exception.BoxProcessException;
import net.anotheria.anosite.handler.exception.BoxSubmitException;
import net.anotheria.anosite.localization.LocalizationMap;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Adapter style implementation of a boxhandler.
 * @author another
 *
 */
public abstract class AbstractBoxHandler implements BoxHandler{
	/**
	 * Log. Each subclass has indirect access to this log via getLog. The log is named by the subclass and created in the constructor.
	 */
	private Logger log;
	/**
	 * A resource service instance.
	 */
	private static IASResourceDataService resourceService;
	/**
	 * Init.
	 */
	static {
		try {
			resourceService = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(AbstractBoxHandler.class).error(MarkerFactory.getMarker("FATAL"), "IASResourceDataService init failure", e);
		}
	}
	
	/**
	 * Constructor for extending classes.
	 */
	protected AbstractBoxHandler(){
		log = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * Returns the log instance. This way each handler has a useable log.
	 * @return Logger.
	 */
	protected Logger getLog(){
		return log;
	}
	/**
	 * Returns ResponseContinue.INSTANCE.
	 */
	@Override public BoxHandlerResponse process(HttpServletRequest req, HttpServletResponse res, Box box, BoxBean bean)  throws BoxProcessException{
		return ResponseContinue.INSTANCE;
	}

	/**
	 * Returns ResponseContinue.INSTANCE.
	 */
	@Override public BoxHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, Box box)  throws BoxSubmitException{
		return ResponseContinue.INSTANCE;
	}
	
	protected static IASResourceDataService getResourceDataService(){
		return resourceService;
	}

	/**
	 * Used to put attribute to current request till next request
	 * 
	 * @param name attribute name
	 * @param attribute attribute value
	 */
	protected void sendAttributeToPage(String name, Object attribute){
		((APISessionImpl)APICallContext.getCallContext().getCurrentSession()).addAttributeToActionScope(name, attribute);
	}
	
	/**
	 * Returns a text resource by its name.
	 * 
	 * @param name
	 *            - resource name
	 * @return {@link String} text resource or warning about missing resource
	 */
	protected static String getTextResource(String name) {
		try {
			// Try to find resource with given key in LocalizationMap
			LocalizationMap localization = (LocalizationMap) APICallContext.getCallContext().getAttribute(LocalizationMap.CALL_CONTEXT_SCOPE_NAME);
			String txt = localization != null ? localization.getMessage(name) : null;
			if (!StringUtils.isEmpty(txt))
				return txt;

			List<TextResource> resources = getResourceDataService().getTextResourcesByProperty(TextResource.PROP_NAME, name);
			if (resources == null || resources.size() == 0)
				return "Missing key: " + name;

			return resources.get(0).getValue();
		} catch (ASGRuntimeException e) {
			String message = "getTextResourceByName(" + name + ") fail.";
			LoggerFactory.getLogger(AbstractBoxHandler.class).error(message, e);
			throw new RuntimeException(message, e);
		}
	}
}
