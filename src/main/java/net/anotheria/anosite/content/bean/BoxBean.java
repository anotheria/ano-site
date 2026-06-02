package net.anotheria.anosite.content.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a box for the jsp usage.
 * @author lrosenberg
 */
public class BoxBean {
	
	/**
	 * The name under which the BoxBean is stored in the call context to allow access by the variable processors.
	 */
	public static final String CALL_CONTEXT_SCOPE_NAME = BoxBean.class.getName();
	
	/**
	 * The type of the box.
	 */
	private BoxTypeBean type;
	/**
	 * List with subboxes.
	 */
	private List<BoxBean> subboxes;
	/**
	 * List with mediaLinks.
	 */
	private List<MediaLinkBean> mediaLinks;
	/**
	 * List with scripts.
	 */
	private List<ScriptBean> scripts;
	/**
	 * Value of the cssClass attribute.
	 */
	private String cssClass;
	/**
	 * Value of the content attribute.
	 */
	private String content;
	/**
	 * Value of the name attribute.
	 */
	private String name;
	/**
	 * Document id of the box.
	 */
	private String id;
	/**
	 * Value of parameter1.
	 */
	private String parameter1;
	/**
	 * Value of parameter2.
	 */
	private String parameter2;
	/**
	 * Value of parameter3.
	 */
	private String parameter3;
	/**
	 * Value of parameter4.
	 */
	private String parameter4;
	/**
	 * Value of parameter5.
	 */
	private String parameter5;
	/**
	 * Value of parameter6.
	 */
	private String parameter6;
	/**
	 * Value of parameter7.
	 */
	private String parameter7;
	/**
	 * Value of parameter8.
	 */
	private String parameter8;
	/**
	 * Value of parameter9.
	 */
	private String parameter9;
	/**
	 * Value of parameter10.
	 */
	private String parameter10;
	
	/**
	 * Attributes assigned to the box.
	 */
	private AttributeMap attributes;
	
	/**
	 * Creates a new BoxBean.
	 */
	public BoxBean(){
		subboxes = new ArrayList<BoxBean>();
		mediaLinks = new ArrayList<MediaLinkBean>();
		scripts = new ArrayList<ScriptBean>();
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParameter1() {
		return parameter1;
	}
	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}
	public String getParameter10() {
		return parameter10;
	}
	public void setParameter10(String parameter10) {
		this.parameter10 = parameter10;
	}
	public String getParameter2() {
		return parameter2;
	}
	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}
	public String getParameter3() {
		return parameter3;
	}
	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
	}
	public String getParameter4() {
		return parameter4;
	}
	public void setParameter4(String parameter4) {
		this.parameter4 = parameter4;
	}
	public String getParameter5() {
		return parameter5;
	}
	public void setParameter5(String parameter5) {
		this.parameter5 = parameter5;
	}
	public String getParameter6() {
		return parameter6;
	}
	public void setParameter6(String parameter6) {
		this.parameter6 = parameter6;
	}
	public String getParameter7() {
		return parameter7;
	}
	public void setParameter7(String parameter7) {
		this.parameter7 = parameter7;
	}
	public String getParameter8() {
		return parameter8;
	}
	public void setParameter8(String parameter8) {
		this.parameter8 = parameter8;
	}
	public String getParameter9() {
		return parameter9;
	}
	public void setParameter9(String parameter9) {
		this.parameter9 = parameter9;
	}
	public List<BoxBean> getSubboxes() {
		return subboxes;
	}
	public void setSubboxes(List<BoxBean> subboxes) {
		this.subboxes = subboxes;
	}
	public BoxTypeBean getType() {
		return type;
	}
	public void setType(BoxTypeBean type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getRenderer(){
		return type.getRenderer();
	}
	
	public String getTypeName(){
		return type.getName();
	}

	public AttributeMap getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributeMap attributes) {
		this.attributes = attributes;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public List<MediaLinkBean> getMediaLinks() {
		return mediaLinks;
	}

	public void setMediaLinks(List<MediaLinkBean> mediaLinks) {
		this.mediaLinks = mediaLinks;
	}
	
	public List<ScriptBean> getScripts() {
		return scripts;
	}

	public void setScripts(List<ScriptBean> scripts) {
		this.scripts = scripts;
	}
	
	@Override public boolean equals(Object anotherObj){
		if(!(anotherObj instanceof BoxBean))
			return false;
		return id.equals(((BoxBean)anotherObj).getId());
	}
	
	@Override public int hashCode(){
		return id.hashCode();
	}
	
	@Override public String toString(){
		String ret = "";
		ret += "BoxBean " + id;
		return ret;
	}

}
