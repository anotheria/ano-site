package net.anotheria.anosite.content.bean;

public class BreadCrumbItemBean {
	private String link;
	private String title;
	private boolean isClickable;
	public boolean isClickable() {
		return isClickable;
	}
	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String toString(){
		return "["+isClickable+"] "+getTitle();
	}
}
