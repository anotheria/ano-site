package net.anotheria.anosite.content.bean;

public class BoxTypeBean {
	private String renderer;
	private String name;
	
	public BoxTypeBean(String aName, String rendererpage){
		this.name = aName;
		setRenderer(rendererpage);
	}
	
	public BoxTypeBean(){
		
	}
	
	
	public void setRenderer(String aRendererPage){
		if (!aRendererPage.startsWith("/"))
			aRendererPage = "/net/anotheria/anosite/layout/renderer/"+aRendererPage;
		if (!aRendererPage.endsWith(".jsp"))
			aRendererPage += ".jsp";
		renderer = aRendererPage;
	}
	
	public String getRenderer(){
		return renderer;
	}
	
	public String toString(){
		return name+" - "+renderer; 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
