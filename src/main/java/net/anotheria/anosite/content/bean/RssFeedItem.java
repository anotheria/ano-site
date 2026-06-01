package net.anotheria.anosite.content.bean;

public interface RssFeedItem {
	public String getTitle();
	
	public String getLink();
	
	public String getThumbnailUrl();
	
	public String getContentUrl();
	
	public String getGuid();
	
	public boolean isPermalink();
}
