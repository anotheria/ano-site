package net.anotheria.anosite.content.servlet;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.shared.InternalResponseCode;

public class InternalBoxBeanWithRedirectResponse extends InternalBoxBeanResponse{
	private String redirectUrl;
	
	public InternalBoxBeanWithRedirectResponse(BoxBean bean, String aRedirectUrl){
		super(InternalResponseCode.CONTINUE_AND_REDIRECT, bean);
		redirectUrl = aRedirectUrl;
	}
	
	public String getRedirectUrl(){
		return redirectUrl;
	}
}
