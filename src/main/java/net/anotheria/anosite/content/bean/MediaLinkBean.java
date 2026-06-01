package net.anotheria.anosite.content.bean;

public class MediaLinkBean {

	private String id;
	private String name;
	private String href;
	private String type;
	private String media;
	private String rel;
	private String rev;
	private String charset;
	private String hreflang;
	private String browserFiltering;
	private String onload;

	public MediaLinkBean() {
	}

	public MediaLinkBean(final String aId) {
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

	public String getHref() {
		return href;
	}

	public void setHref(final String aHref) {
		this.href = aHref;
	}

	public String getType() {
		return type;
	}

	public void setType(final String aType) {
		this.type = aType;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(final String aMedia) {
		this.media = aMedia;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(final String aRel) {
		this.rel = aRel;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(final String aRev) {
		this.rev = aRev;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(final String aCharset) {
		this.charset = aCharset;
	}

	public String getHreflang() {
		return hreflang;
	}

	public void setHreflang(final String aHreflang) {
		this.hreflang = aHreflang;
	}

	public String getBrowserFiltering() {
		return browserFiltering;
	}

	public void setBrowserFiltering(final String aBrowserFiltering) {
		this.browserFiltering = aBrowserFiltering;
	}

	public String getOnload() {
		return onload;
	}

	public void setOnload(String aOnload) {
		this.onload = aOnload;
	}

	@Override
	public boolean equals(Object anotherObj) {
		if (!(anotherObj instanceof MediaLinkBean))
			return false;
		return id.equals(((MediaLinkBean) anotherObj).getId());
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
