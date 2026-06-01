package net.anotheria.anosite.access;

import net.anotheria.anoplass.api.APIException;

/**
 * {@link AnoSiteAccessAPI} main exception.
 * 
 * @author Alexandr Bolbat
 */
public class AnoSiteAccessAPIException extends APIException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 6781697641130775376L;

	/**
	 * Default constructor.
	 */
	public AnoSiteAccessAPIException() {
	}

	/**
	 * Public constructor.
	 * 
	 * @param message
	 *            - exception message
	 */
	public AnoSiteAccessAPIException(final String message) {
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
	public AnoSiteAccessAPIException(final String message, final Exception cause) {
		super(message, cause);
	}

}
