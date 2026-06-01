package net.anotheria.anosite.tags.content;

import net.anotheria.anosite.content.bean.AttributeMap;
import net.anotheria.anosite.content.bean.BoxBean;

/**
 * Writes the value for the attribute "name" of the current box. If no value is specified, writes out the defaultValue.
 * @author lrosenberg
 *
 */
public class BoxAttributeTag extends AbstractAttributeTag {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6330988588913126149L;

	@Override
	protected AttributeMap getAttributeMap() {
		BoxBean box = (BoxBean) pageContext.getAttribute("box");
		AttributeMap attributes = box.getAttributes();
		return attributes != null ? attributes : new AttributeMap(); 
	}

}
