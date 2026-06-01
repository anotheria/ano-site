package net.anotheria.anosite.access;

/**
 * {@link AnoSiteAccessAPI} exception.
 * 
 * @author Alexandr Bolbat
 */
public class AccessOperationNotFoundAPIException extends AnoSiteAccessAPIException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -45851802929787931L;

	/**
	 * Default constructor.
	 */
	public AccessOperationNotFoundAPIException() {
	}

	/**
	 * Public constructor.
	 * 
	 * @param message
	 *            - exception message
	 */
	public AccessOperationNotFoundAPIException(final String message) {
		super(message);
	}

	/**
	 * Public constructor.
	 * 
	 * @param message
	 *            - exception message
	 * @param cause
	 *            - exception cause
	 */
	public AccessOperationNotFoundAPIException(final String message, final Exception cause) {
		super(message, cause);
	}

}
