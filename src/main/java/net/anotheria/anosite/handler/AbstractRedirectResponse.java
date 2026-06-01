package net.anotheria.anosite.handler;

public abstract  class AbstractRedirectResponse extends BoxHandlerResponse{
	private String redirectTarget;
	
	protected AbstractRedirectResponse(String aRedirectTarget){
		redirectTarget = aRedirectTarget;
	}
	
	public String getRedirectTarget(){
		return redirectTarget;
	}
}
