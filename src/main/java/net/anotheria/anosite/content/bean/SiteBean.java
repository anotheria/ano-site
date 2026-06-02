package net.anotheria.anosite.content.bean;
/**
 * This bean represents the site object from the cms prepared for usage in a (jsp) template.
 * @author lrosenberg
 */
public class SiteBean {
	/**
	 * Site name.
	 */
	private String name;
	/**
	 * Site title (html-head).
	 */
	private String title;
	/**
	 * Site subtitle.
	 */
	private String subtitle;
	/**
	 * Keywords for the head meta section.
	 */
	private String keywords;
	/**
	 * Description for the head meta section.
	 */
	private String description;
	/**
	 * Link to the start page to render a "Home" or "Start" button.
	 */
	private String linkToStartPage;
	/**
	 * If true, the site should offer a language selector.
	 */
	private boolean languageSelector;
	/**
	 * Url to call for the search action if applicable.
	 */
	private String searchTarget;

    /**
     * Sitebean - Site logo. Path to logo - itself.
     */
    private String logo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLinkToStartPage() {
		return linkToStartPage;
	}
	public void setLinkToStartPage(String linkToStartPage) {
		this.linkToStartPage = linkToStartPage;
	}
	public boolean isLanguageSelector() {
		return languageSelector;
	}
	public void setLanguageSelector(boolean languageSelector) {
		this.languageSelector = languageSelector;
	}
	public boolean isShowSearchDialog() {
		return searchTarget!=null && searchTarget.length()>0;
	}
	public String getSearchTarget() {
		return searchTarget;
	}
	public void setSearchTarget(String searchTarget) {
		this.searchTarget = searchTarget;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
