package net.anotheria.anosite.content.variables;

import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anosite.content.bean.AttributeBean;
import net.anotheria.anosite.content.bean.AttributeMap;
/**
 * This processor supports a various range of attributes:
 * PREFIX_API_CALL_CONTEXT_ATTRIBUTE
 * PREFIX_API_SESSION_ATTRIBUTE
 * PREFIX_REQUEST_ATTRIBUTE
 * PREFIX_SESSION_ATTRIBUTE
 * PREFIX_SESSION_AND_DELETE_ATTRIBUTE
 * PREFIX_CONTEXT_ATTRIBUTE
 * PREFIX_BOX_ATTRIBUTE
 * PREFIX_PAGE_ATTRIBUTE
 * @author lrosenberg
 *
 */
public class AttributeProcessor implements VariablesProcessor {

	@Override public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
		Object ret = null;
		if("NONE".equals(defValue))
			defValue = "";
		if (prefix.equals(DefinitionPrefixes.PREFIX_API_CALL_CONTEXT_ATTRIBUTE))
			ret = APICallContext.getCallContext().getAttribute(variable);
		if (prefix.equals(DefinitionPrefixes.PREFIX_API_SESSION_ATTRIBUTE))
			ret = APICallContext.getCallContext().getCurrentSession().getAttribute(variable);
		if (prefix.equals(DefinitionPrefixes.PREFIX_REQUEST_ATTRIBUTE))
			ret = req.getAttribute(variable);
		if (prefix.equals(DefinitionPrefixes.PREFIX_SESSION_ATTRIBUTE))
			ret = req.getSession().getAttribute(variable);
		if (prefix.equals(DefinitionPrefixes.PREFIX_SESSION_AND_DELETE_ATTRIBUTE)){
			ret = req.getSession().getAttribute(variable);
			req.getSession().removeAttribute(variable);
		}
		if (prefix.equals(DefinitionPrefixes.PREFIX_CONTEXT_ATTRIBUTE))
			ret = req.getSession().getServletContext().getAttribute(variable);
		
		if (DefinitionPrefixes.PREFIX_BOX_ATTRIBUTE.equals(prefix)){
			AttributeBean bean = ((AttributeMap)APICallContext.getCallContext().getAttribute(AttributeMap.BOX_ATTRIBUTES_CALL_CONTEXT_SCOPE_NAME)).getAttribute(variable); 
			ret = bean == null ? null : bean.getValue();
		}
		
		if (DefinitionPrefixes.PREFIX_PAGE_ATTRIBUTE.equals(prefix)){
			AttributeBean bean = ((AttributeMap)APICallContext.getCallContext().getAttribute(AttributeMap.PAGE_ATTRIBUTES_CALL_CONTEXT_SCOPE_NAME)).getAttribute(variable); 
			ret = bean == null ? null : bean.getValue();
		}
		
		return ret == null ? defValue : ret.toString();
	}
	
}
