package net.anotheria.anosite.handler;

import net.anotheria.anosite.shared.InternalResponseCode;

public class ResponseStop extends BoxHandlerResponse{
	public InternalResponseCode getResponseCode(){
		return InternalResponseCode.STOP;
	}
}
