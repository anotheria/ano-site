package net.anotheria.anosite.content.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * A map with attributes assigned to a cms object for further processing (for example by variable processors).
 * @author lrosenberg
 *
 */
public class AttributeMap {
	/**
	 * The map with attribute beans.
	 */
	private Map<String, AttributeBean> attributes;
	/**
	 * The name under which the box attribute map is stored in the call context to allow access by the variable processors.
	 */
	public static final String BOX_ATTRIBUTES_CALL_CONTEXT_SCOPE_NAME = "boxAttributes" + AttributeMap.class.getName();
	/**
	 * The name under which the page attribute map is stored in the call context to allow access by the variable processors.
	 */
	public static final String PAGE_ATTRIBUTES_CALL_CONTEXT_SCOPE_NAME = "pageAttributes" + AttributeMap.class.getName();
	/**
	 * Creates a new attribute map.
	 */
	public AttributeMap(){
		attributes = new HashMap<String, AttributeBean>();
	}
	/**
	 * Returns the attribute under given name.
	 * @param key  TODO dummy comment for javadoc.
	 * @return  TODO dummy comment for javadoc.
	 */
	public AttributeBean getAttribute(String key){
		return attributes.get(key);
	}
	/**
	 * Sets the attribute. If override is true an old value will be overriden.
	 * @param b  TODO dummy comment for javadoc.
	 * @param override TODO dummy comment for javadoc.
	 */
	public void setAttribute(AttributeBean b, boolean override){
		AttributeBean old = attributes.get(b.getKey());
		if (old == null || override)
			attributes.put(b.getKey(), b);
	}
	/**
	 * Sets the attribute. Note, attributes aren't overriden once set.
	 * @param b  TODO dummy comment for javadoc.
	 */
	public void setAttribute(AttributeBean b){
		setAttribute(b, false);
	}
	
	@Override public String toString(){
		return attributes.toString();
	}
}
