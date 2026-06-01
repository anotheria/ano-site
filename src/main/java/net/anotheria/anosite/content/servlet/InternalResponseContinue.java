package net.anotheria.anosite.content.servlet;

import net.anotheria.anosite.shared.InternalResponseCode;
/**
 * A simple response which signalizes that everything is ok, and the execution should be performed as intended.
 * @author another
 *
 */
public class InternalResponseContinue extends InternalResponse{
	/**
	 * Creates a new internal response continue object.
	 */
	public InternalResponseContinue(){
		super(InternalResponseCode.CONTINUE);
	}
}
