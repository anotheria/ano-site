package net.anotheria.anosite.content.variables;

import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.anodoc.util.context.BrandConfig;
import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.content.bean.AttributeBean;
import net.anotheria.anosite.content.bean.AttributeMap;
import net.anotheria.anosite.gen.aswebdata.data.Attribute;
import net.anotheria.anosite.gen.aswebdata.service.ASWebDataServiceException;
import net.anotheria.anosite.gen.aswebdata.service.IASWebDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

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
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AttributeProcessor.class);
	/**
	 * {@link IASWebDataService} instance.
	 */
	private static IASWebDataService iasWebDataService;

	//init service
	static {
		try {
			iasWebDataService = MetaFactory.get(IASWebDataService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "IASWebDataService init failure", e);
		}
	}

	@Override
	public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
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

		if (DefinitionPrefixes.PREFIX_BRAND_ATTRIBUTE.equals(prefix)) {
			BrandConfig brandConfig = ContextManager.getCallContext().getBrandConfig();
			if (brandConfig != null) {
				for (String attrId: brandConfig.getAttributes()) {
					try {
						Attribute attribute = iasWebDataService.getAttribute(attrId);
						if (attribute.getKey().equals(variable)) {
							ret = attribute.getValue();
							break;
						}
					} catch (ASWebDataServiceException e) {
						LOGGER.warn("Unable to check attribute with id [{}] for brand.{}", attrId, e.getMessage());
					}
				}
			}
		}
		
		return ret == null ? defValue : ret.toString();
	}
	
}
