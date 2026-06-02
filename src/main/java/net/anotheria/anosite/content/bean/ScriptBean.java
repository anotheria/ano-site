package net.anotheria.anosite.content.bean;

public class ScriptBean {

	private String id;
	private String name;
	private String link;
	private String content;
	private String browserFiltering;

	public ScriptBean() {
	}

	public ScriptBean(final String aId) {
		this.id = aId;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		this.id = aId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		this.name = aName;
	}

	public String getLink() {
		return link;
	}

	public void setLink(final String aLink) {
		this.link = aLink;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String aContent) {
		this.content = aContent;
	}

	public String getBrowserFiltering() {
		return browserFiltering;
	}

	public void setBrowserFiltering(final String aBrowserFiltering) {
		this.browserFiltering = aBrowserFiltering;
	}

	@Override
	public boolean equals(Object anotherObj) {
		if (!(anotherObj instanceof ScriptBean))
			return false;
		return id.equals(((ScriptBean) anotherObj).getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		String ret = "";
		ret += "MediaLinkBean " + id;
		return ret;
	}

}
