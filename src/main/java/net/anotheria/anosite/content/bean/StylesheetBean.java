package net.anotheria.anosite.content.bean;
/**
 * Holds a link to a styleshit useable by a template.
 * @author lrosenberg
 *
 */
public class StylesheetBean {
	/**
	 * The link to the template.
	 */
	private String link;
	/**
	 * Creates a new stylesheetbean linked to the style sheet document with given id.
	 * @param styleObjectId
	 */
	public StylesheetBean(String styleObjectId){
		link = "styles/"+styleObjectId+".css";
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
