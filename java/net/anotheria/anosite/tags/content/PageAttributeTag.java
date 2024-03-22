package net.anotheria.anosite.tags.content;

import jakarta.servlet.jsp.PageContext;

import net.anotheria.anosite.content.bean.AttributeMap;
import net.anotheria.anosite.content.bean.PageBean;

/**
 * Writes the value for the attribute "name" of the current page. If no value is specified, writes out the defaultValue.
 * @author dzhmud
 *
 */
public class PageAttributeTag extends AbstractAttributeTag {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1779046700230951351L;

	@Override
	protected AttributeMap getAttributeMap() {
		PageBean page = (PageBean) pageContext.getAttribute("page", PageContext.REQUEST_SCOPE);
		AttributeMap attributes = page.getAttributes();
		return attributes != null ? attributes : new AttributeMap(); 
	}

}
