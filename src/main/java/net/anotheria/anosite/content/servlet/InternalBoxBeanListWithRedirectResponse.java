package net.anotheria.anosite.content.servlet;

import java.util.List;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.shared.InternalResponseCode;

public class InternalBoxBeanListWithRedirectResponse extends InternalBoxBeanListResponse{
	private String redirectUrl;
	
	public InternalBoxBeanListWithRedirectResponse(List<BoxBean> beans, String aRedirectUrl){
		super(InternalResponseCode.CONTINUE_AND_REDIRECT, beans);
		redirectUrl = aRedirectUrl;
	}
	
	public String getRedirectUrl(){
		return redirectUrl;
	}
}
