package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Empty validator settings.
 * 
 * @author Alexandr Bolbat
 */
public class EmptyValidatorSettings implements ValidatorSettings {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -6841822238596021396L;

	/**
	 * JSON name for this settings.
	 */
	public static final String JSON_SETTINGS_NAME = "validate-empty";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_ERROR_KEY_NAME = "notValidMsg";

	/**
	 * Error key if field value empty.
	 */
	private String errorKey = "";

	/**
	 * Private constructor.
	 */
	private EmptyValidatorSettings() {
	}

	/**
	 * Create new instance of this class.
	 * 
	 * @return {@link EmptyValidatorSettings}
	 */
	public static final EmptyValidatorSettings create() {
		return new EmptyValidatorSettings();
	}

	/**
	 * Set error key for not valid field value if its empty.
	 * 
	 * @param aErrorKey
	 *            - error key
	 * @return {@link EmptyValidatorSettings}
	 */
	public EmptyValidatorSettings setErrorKey(String aErrorKey) {
		this.errorKey = aErrorKey;
		return this;
	}

	@Override
	public String getName() {
		return JSON_SETTINGS_NAME;
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}

	@Override
	public JSONObject toJSON() {
		try {
			JSONObject result = new JSONObject();
			result.put(JSON_SETTINGS_ERROR_KEY_NAME, errorKey);

			return result;
		} catch (JSONException e) {
			throw new RuntimeException("Preparing JSON fail.", e);
		}
	}

}
