package net.anotheria.anosite.handler;

import net.anotheria.anosite.shared.InternalResponseCode;
/**
 * Response continue. Signalizes the outer code to continue processing.
 * @author lrosenberg
 */
public class ResponseContinue extends BoxHandlerResponse{
	/**
	 * A stateless instance which can be used as return value to reduce memory pollution. 
	 */
	public static final BoxHandlerResponse INSTANCE = new ResponseContinue();
	
	public InternalResponseCode getResponseCode(){
		return InternalResponseCode.CONTINUE;
	}
}
