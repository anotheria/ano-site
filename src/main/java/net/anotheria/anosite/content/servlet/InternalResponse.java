package net.anotheria.anosite.content.servlet;

import net.anotheria.anosite.handler.BoxHandlerResponse;
import net.anotheria.anosite.shared.InternalResponseCode;

/**
 * Used to transform more than one parameter about execution back to the caller. Usually contains instructions for the caller what to do next. For example
 * an internal response can contain the instruction to abort execution or to continue normally.
 * @author another
 *
 */
public class InternalResponse {
	/**
	 * Responses code.
	 */
	private InternalResponseCode code;
	
	public InternalResponse(){
		
	}
	
	public InternalResponse(InternalResponseCode aCode){
		code = aCode;
	}
	
	public InternalResponse(BoxHandlerResponse handlerResponse){
		code = handlerResponse.getResponseCode();
	}

	public InternalResponseCode getCode() {
		return code;
	}

	public void setCode(InternalResponseCode code) {
		this.code = code;
	}
	
	public boolean canContinue(){
		return code == InternalResponseCode.CONTINUE || code == InternalResponseCode.CONTINUE_AND_REDIRECT || code == InternalResponseCode.ERROR_AND_CONTINUE; 
	}

	@Override
	public String toString(){
		return "code: "+code;
	}
}
