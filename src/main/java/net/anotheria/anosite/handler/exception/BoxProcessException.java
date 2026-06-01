package net.anotheria.anosite.handler.exception;

/**
 * BoxProcessException. As BoxHandleException, which occurs on process operation.
 *
 * @author h3llka
 */
public class BoxProcessException extends BoxHandleException{

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message string message
	 */
	public BoxProcessException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 * @param cause exception itself
	 */
	public BoxProcessException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 *
	 * @param cause exception itself
	 */
	public BoxProcessException(Exception cause) {
		super("Error while processing BOX", cause);
	}
}
