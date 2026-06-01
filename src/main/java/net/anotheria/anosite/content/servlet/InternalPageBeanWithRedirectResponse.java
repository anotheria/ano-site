package net.anotheria.anosite.content.servlet;

import net.anotheria.anosite.content.bean.PageBean;
import net.anotheria.anosite.shared.InternalResponseCode;
/**
 * An internal response with a redirect.
 * @author another
 *
 */
public class InternalPageBeanWithRedirectResponse extends InternalPageBeanResponse{
	/**
	 * Target redirect.
	 */
	private String redirectUrl;
	
	public InternalPageBeanWithRedirectResponse(PageBean bean, String aRedirectUrl){
		super(InternalResponseCode.CONTINUE_AND_REDIRECT, bean);
		redirectUrl = aRedirectUrl;
	}
	/**
	 * Returns the redirect url.
	 * @return the redirect url.
	 */
	public String getRedirectUrl(){
		return redirectUrl;
	}
}
