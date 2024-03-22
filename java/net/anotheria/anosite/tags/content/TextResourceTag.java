package net.anotheria.anosite.tags.content;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.content.variables.VariablesUtility;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.localization.LocalizationMap;
import net.anotheria.anosite.util.AnositeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes out the content of the resource. Writes out a link to the resource in edit mode.
 * @author another
 *
 */
public class TextResourceTag extends BaseResourceTag{
	/**
	 * {@link Logger} instance for view used keys.
	 */
	private static final Logger TEXT_RESOURCE_LOGGER = LoggerFactory.getLogger("TextResourceLog");

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 6849361605149404293L;
	
	/**
	 * Direct reference to resource key (name).
	 */
	private String key;

	@Override public int doEndTag() throws JspException {

		String myKey = null;
		if (key!=null){
			myKey = key;
		}else{
			myKey = (String)lookup();
		}

		boolean editable = false;
		TEXT_RESOURCE_LOGGER.info(key);
		
		//Try to find resource with given key in LocalizationMap
		LocalizationMap localization = (LocalizationMap)APICallContext.getCallContext().getAttribute(LocalizationMap.CALL_CONTEXT_SCOPE_NAME);
		String txt = localization != null? localization.getMessage(myKey): null;
		
		if(txt != null){
			write(txt);
			return SKIP_BODY;
		}
						
		//Ok, resource in LocalizationMap not found. Loading from TextResources
		TextResource resource = getTextResourceByName(myKey);
		if (resource==null)
			txt = "Missing key: "+myKey;
		else
			txt = resource.getValue();

		HttpSession session = pageContext.getSession();
		if (session!=null && resource!=null && session.getAttribute(AnositeConstants.SA_EDIT_MODE_FLAG)!=null)
			editable = true;

		if (editable){
			//For easy editing decorate resource with link in the CMS
			String link = "<a href="+quote("cms/asresourcedataTextResourceEdit?ts="+System.currentTimeMillis()+"&pId="+resource.getId())+" target="+quote("_blank")+">E</a>&nbsp;";
			write(link);
		}
		
		//Hook to replace variables
		txt = VariablesUtility.replaceVariables((HttpServletRequest) pageContext.getRequest(), txt);
		
		write(txt);


		return SKIP_BODY;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	protected BoxBean getBox(){
		return (BoxBean) pageContext.findAttribute("__box");
	}
}
