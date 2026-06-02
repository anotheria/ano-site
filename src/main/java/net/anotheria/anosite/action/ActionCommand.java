package net.anotheria.anosite.action;

import net.anotheria.anosite.action.servlet.cms.ActionHelper;
import net.anotheria.anosite.gen.ascustomaction.data.ActionMappingDef;
import net.anotheria.anosite.gen.shared.data.ActionCommandDefUtils;

/**
 * ActionCommand is the result of an action execution. By returning an ActionCommand the action 'tells' the ActionServlet how to perform further.
 * @author another
 *
 */
public class ActionCommand {
	/**
	 * Target url.
	 */
	private String url;
	/**
	 * Target page.
	 */
	private String page;
	/**
	 * Type of command (redirect, forward or none).
	 */
	private CommandType type;
	/**
	 * Parameters.
	 */
	private String parameters;
	
	public ActionCommand() {
	}
	
	public ActionCommand(ActionMappingDef def) {
		url = def.getUrl();
		//HOT FIX: avoids NoSuchDocumentException if page is not set
		page = def.getPage() != null && def.getPage().length() > 0? ActionHelper.getPageNameForAction(def): null;
		parameters = def.getParameters();
		type = cms2enum(def.getCommand());
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public CommandType getType() {
		return type;
	}
	public void setType(CommandType type) {
		this.type = type;
	}
	
	/**
	 * Returns the target for the redirect or forward.
	 * First the url parameter is checked, if no url parameter is specified, page parameter is considered and the url to the specified page is returned.
	 * @return the target for the redirect or forward.
	 */
	public String getTarget(){
		String targetUrl =  url == null || url.trim().length() == 0 ? 
				getPageUrl() : url;
		if (parameters!=null && parameters.length()>0){
			targetUrl += targetUrl.indexOf('?') == -1 ? '?' : '&';
			targetUrl += parameters;
		}
		return targetUrl;
	}
	
	//fix this someday
	private String getPageUrl(){
		return getPage()+".html";
	}
	
	public static final CommandType cms2enum(int value){
		switch(value){
		case ActionCommandDefUtils.Forward:
			return CommandType.Forward;
		case ActionCommandDefUtils.Redirect:
			return CommandType.Redirect;
		}
		return CommandType.None;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public void addParameters(String someParameters){
		if (parameters!=null && parameters.length()>0){
			if (!(someParameters.charAt(0)=='&'))
				parameters+='&';
			parameters += someParameters;
		}else{
			parameters = someParameters;
		}
	}
	
	@Override public String toString(){
		return getType()+" "+getTarget();
	}
	
	
}
