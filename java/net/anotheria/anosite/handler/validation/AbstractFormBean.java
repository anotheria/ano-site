package net.anotheria.anosite.handler.validation;

import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Form definition bean used in {@link AbstractValidationBoxHandler}.
 * 
 * @author Alexandr Bolbat
 */
public abstract class AbstractFormBean implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 3724770407071535482L;

	/**
	 * Utility constant with empty {@link String} value.
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * Utility constant with empty {@link Number} value.
	 */
	public static final Number EMPTY_NUMBER = -1;

	/**
	 * Utility constant with empty {@link Boolean} value.
	 */
	public static final boolean EMPTY_BOOLEAN = false;

	/**
	 * Utility constant with empty {@link List} value.
	 */
	public static final List<?> EMPTY_LIST = Collections.emptyList();

	/**
	 * Utility constant with empty {@link Set} value.
	 */
	public static final Set<?> EMPTY_SET = Collections.emptySet();

	/**
	 * Internal storage for form data.
	 */
	private final Map<String, Object> formData = new HashMap<String, Object>();

	/**
	 * Get set of all form fields names. By default it use key set from internal storage.
	 *
	 * @return Set of {@link String}
	 */
	public Set<String> getFieldsNames(){
		return formData.keySet();
	}

	/**
	 * Get form field value by form field name. By default it use internal storage.
	 *
	 * @param fieldName
	 *            - form field name
	 * @return {@link Object}
	 */
	public Object getFieldValue(final String fieldName) {
		return readField(fieldName);
	}

	/**
	 * Save field value to form data storage.
	 * 
	 * @param name
	 *            - field name
	 * @param value
	 *            - field value
	 */
	protected final void saveField(final String name, final Object value) {
		formData.put(name, value);
	}

	/**
	 * Read field value from form data storage. Return value or <code>null</code>.
	 * 
	 * @param name
	 *            - field name
	 * @return {@link Object}
	 */
	protected final Object readField(final String name) {
		return formData.get(name);
	}

	/**
	 * Read field value from form data storage in real type. Can be returned with some default value if real value not exist in storage.
	 * 
	 * @param name field name.
	 * @param fieldType field value type class.
	 * @param defaultValue  TODO dummy comment for javadoc.
	 * @param <T>  TODO dummy comment for javadoc.
	 * @return T  TODO dummy comment for javadoc.
	 */
	protected final <T> T readField(final String name, Class<T> fieldType, T defaultValue) {
		Object value = readField(name);
		if (value == null)
			return defaultValue;

		return fieldType.cast(value);
	}

	/**
	 * Any bean extended from this abstract class must have possibility to initialize by self from request.
	 * 
	 * @param req request
	 */
	protected abstract void prepare(final HttpServletRequest req);

}
