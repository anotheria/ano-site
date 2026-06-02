package net.anotheria.anosite.action.servlet;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.access.AnoSiteAccessAPI;
import net.anotheria.anosite.action.Action;
import net.anotheria.anosite.action.ActionCommand;
import net.anotheria.anosite.action.ActionMapping;
import net.anotheria.anosite.action.servlet.cms.ActionFactory;
import net.anotheria.anosite.action.servlet.cms.ActionHelper;
import net.anotheria.anosite.gen.ascustomaction.data.ActionMappingDef;
import net.anotheria.anosite.shared.presentation.servlet.BaseAnoSiteServlet;
import net.anotheria.anosite.util.ModelObjectMapper;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ActionServlet extends BaseAnoSiteServlet {

	/**
	 * {@link Logger} instance.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(ActionServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link AnoSiteAccessAPI} instance.
	 */
	private AnoSiteAccessAPI accessAPI;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		LOGGER.info("Init ActionServlet");
		accessAPI = APIFinder.findAPI(AnoSiteAccessAPI.class);
	}

	@Override
	protected void moskitoDoGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		execute(req, res);
	}

	@Override
	protected void moskitoDoPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		execute(req, res);
	}
	
	private void execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String actionMappingName = getActionMappingName(req);
		out("Calling actionmapping: "+actionMappingName);
		
		ActionMappingDef def = ActionHelper.lookupActionMapping(actionMappingName);
		out("Found def: "+def);
		
		if (def==null){
			LOGGER.warn("ActionMapping not found: " + actionMappingName);
			return;
		}
		
		// checking access
		try {
			if (!StringUtils.isEmpty(def.getAction()) && def.getAction().toLowerCase().startsWith("c-")) // applicable only for custom actions
				if (!accessAPI.isAllowedForAction(def.getAction().substring(2))) {
					res.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
		} catch (Exception e) {
			LOGGER.warn("Error in AccessAPI. ActionMappingDef: " + def + ", actionMappingName: " + actionMappingName + ")", e);
		}
		                                                                                                             
		//create mapping:
		ActionMapping mapping = new ActionMapping(def);
		
		
		Action action = ActionFactory.createAction(def);
		if (action==null){
			LOGGER.warn("Couldn't create an action instance...");
			return;
		}		
		
		ModelObjectMapper.map(req, action);
		try{
			ActionCommand ret = action.execute(req, res, mapping);
			out("Action returned: "+ret);
			if (ret==null)
				return;
			
			switch(ret.getType()){
			case Forward:
				RequestDispatcher dispatcher = req.getRequestDispatcher(ret.getTarget());
				dispatcher.forward(req, res);
			case Redirect:
				res.sendRedirect(ret.getTarget());
				return;
			case None:

			}
			
		} catch(Exception e) {
			LOGGER.error("execute", e);
			//send to error page
		}
			
		
	}
	
	private String getActionMappingName(HttpServletRequest req){
		return extractArtifactName(req);
	}
	
	private void out(Object o){
		LOGGER.debug("[ActionServlet] " + o);
	}

}
