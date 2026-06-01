package net.anotheria.anosite.content.servlet;

import net.anotheria.anosite.shared.InternalResponseCode;

public class InternalRedirectResponse extends InternalResponse{
	
	private String url;
	
	public InternalRedirectResponse(String anUrl){
		super(InternalResponseCode.CONTINUE_AND_REDIRECT);
		url = anUrl;
	}
	
	public String getUrl(){
		return url;
	}
}
