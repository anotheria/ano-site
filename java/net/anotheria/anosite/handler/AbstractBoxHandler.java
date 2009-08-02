package net.anotheria.anosite.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceFactory;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.asg.exception.ASGRuntimeException;

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
	private static IASResourceDataService resourceService = ASResourceDataServiceFactory.createASResourceDataService();
	
	/**
	 * Constructor for extending classes.
	 */
	protected AbstractBoxHandler(){
		log = Logger.getLogger(this.getClass());
	}

	/**
	 * Returns the log instance. This way each handler has a useable log.
	 * @return
	 */
	protected Logger getLog(){
		return log;
	}
	/**
	 * Returns ResponseContinue.INSTANCE.
	 */
	@Override public BoxHandlerResponse process(HttpServletRequest req, HttpServletResponse res, Box box, BoxBean bean)  throws ASGRuntimeException{
		return ResponseContinue.INSTANCE;
	}

	/**
	 * Returns ResponseContinue.INSTANCE.
	 */
	@Override public BoxHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, Box box)  throws ASGRuntimeException{
		return ResponseContinue.INSTANCE;
	}
	
	protected static IASResourceDataService getResourceDataService(){
		return resourceService;
	}

}
