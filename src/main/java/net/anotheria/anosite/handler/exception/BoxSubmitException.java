package net.anotheria.anosite.handler.exception;

/**
 * BoxProcessException. As BoxHandleException, which occurs on submit operation.
 *
 * @author h3llka
 */
public class BoxSubmitException extends BoxHandleException{
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message string message
	 */
	public BoxSubmitException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 * @param cause exception itself
	 */
	public BoxSubmitException(String message, Exception cause) {
		super(message, cause);
	}


	/**
	 * Constructor.
	 *
	 * @param cause exception itself
	 */
	public BoxSubmitException(Exception cause) {
		super("Error while submitting  BOX", cause);
	}
}
