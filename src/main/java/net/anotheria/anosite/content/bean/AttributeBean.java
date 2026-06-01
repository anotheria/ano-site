package net.anotheria.anosite.content.bean;

/**
 * A bean that contains the data from a net.anotheria.anosite.gen.aswebdata.data.Attribute for rendering.
 * @author lrosenberg
 *
 */
public class AttributeBean {
	/**
	 * Key property.
	 */
	private String key;
	/**
	 * Name property.
	 */
	private String name;
	/**
	 * Value property.
	 */
	private String value;

	/**
	 * Constructor.
	 */
	public AttributeBean(){
		
	}

	/**
	 * Constructor.
	 * 
	 * @param aKey key
	 * @param aName name
	 * @param aValue value
	 */
	public AttributeBean(String aKey, String aName, String aValue){
		key = aKey;
		name = aName;
		value = aValue;
	}
	
	@Override public String toString(){
		return getName()+": "+getKey()+" = "+getValue();
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
