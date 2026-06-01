package net.anotheria.anosite.handler.validation;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Basic form bean. Use it as example for real form beans.
 * 
 * @author Alexandr Bolbat
 */
public class BasicFormBean extends AbstractFormBean {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 5952785433197663177L;

	/**
	 * Attribute name for first name.
	 */
	public static final String ATTR_NAME_FIRST_NAME = "firstName";

	/**
	 * Attribute name for last name.
	 */
	public static final String ATTR_NAME_LAST_NAME = "lastName";

	@Override
	public Object getFieldValue(String fieldName) {
		return readField(fieldName);
	}

	@Override
	protected void prepare(HttpServletRequest req) {
		saveField(ATTR_NAME_FIRST_NAME, req.getAttribute(ATTR_NAME_FIRST_NAME));
		saveField(ATTR_NAME_LAST_NAME, req.getAttribute(ATTR_NAME_LAST_NAME));
	}

	/**
	 * Get first name in right type.
	 * 
	 * @return {@link String}
	 */
	public String getFirstName() {
		return readField(ATTR_NAME_FIRST_NAME, String.class, EMPTY_STRING);
	}

	/**
	 * Get last name in right type.
	 * 
	 * @return {@link String}
	 */
	public String getLastName() {
		return readField(ATTR_NAME_LAST_NAME, String.class, EMPTY_STRING);
	}

}
