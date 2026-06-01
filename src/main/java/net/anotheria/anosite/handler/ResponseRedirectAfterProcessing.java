package net.anotheria.anosite.handler;

import net.anotheria.anosite.shared.InternalResponseCode;

public class ResponseRedirectAfterProcessing extends AbstractRedirectResponse{
	
	public ResponseRedirectAfterProcessing(String redirectUrl){
		super(redirectUrl);
	}
	
	public InternalResponseCode getResponseCode(){
		return InternalResponseCode.CONTINUE_AND_REDIRECT;
	}
}
