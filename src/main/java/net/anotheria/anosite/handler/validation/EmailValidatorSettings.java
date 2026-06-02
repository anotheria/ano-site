package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Email validator settings.
 * 
 * @author Alexandr Bolbat
 */
public class EmailValidatorSettings implements ValidatorSettings {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -6841822238596021396L;

	/**
	 * JSON name for this settings.
	 */
	public static final String JSON_SETTINGS_NAME = "validate-email";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_VALIDATE_FORMAT_NAME = "validateFormat";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_ERROR_KEY_NAME = "notValidMsg";

	/**
	 * Is need validate email format.
	 */
	private boolean validateFormat = false;

	/**
	 * Error key.
	 */
	private String errorKey = "";

	/**
	 * Private constructor.
	 */
	private EmailValidatorSettings() {
	}

	/**
	 * Create new instance of this class.
	 * 
	 * @return {@link EmailValidatorSettings}
	 */
	public static final EmailValidatorSettings create() {
		return new EmailValidatorSettings();
	}

	/**
	 * Is need validate email format.
	 * 
	 * @param isValidateFormat
	 *            - set is need validate email format
	 * @return {@link EmailValidatorSettings}
	 */
	public EmailValidatorSettings setValidateFormat(boolean isValidateFormat) {
		this.validateFormat = isValidateFormat;
		return this;
	}

	/**
	 * Set error key for not valid field value.
	 * 
	 * @param aErrorKey
	 *            - error key
	 * @return {@link EmailValidatorSettings}
	 */
	public EmailValidatorSettings setErrorKey(String aErrorKey) {
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
			result.put(JSON_SETTINGS_VALIDATE_FORMAT_NAME, validateFormat);
			result.put(JSON_SETTINGS_ERROR_KEY_NAME, errorKey);

			return result;
		} catch (JSONException e) {
			throw new RuntimeException("Preparing JSON fail.", e);
		}
	}

}
