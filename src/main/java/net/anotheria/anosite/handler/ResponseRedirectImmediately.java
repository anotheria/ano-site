package net.anotheria.anosite.handler;

import net.anotheria.anosite.shared.InternalResponseCode;

public class ResponseRedirectImmediately extends AbstractRedirectResponse{
	
	public ResponseRedirectImmediately(String redirectUrl){
		super(redirectUrl);
	}
	
	public InternalResponseCode getResponseCode(){
		return InternalResponseCode.CANCEL_AND_REDIRECT;
	}
}
