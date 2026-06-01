package net.anotheria.anosite.handler.exception;

/**
 * BoxHandleException class. Happens on Box Handle errors.
 *
 * @author h3llka
 */
public class BoxHandleException extends Exception{

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message string message
	 */
	public BoxHandleException(String message){
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 * @param cause exception itself
	 */
	public BoxHandleException(String message, Exception cause){
		super(message,cause);
	}
}
