package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Filled validator settings.
 * 
 * @author Oliver Tonse
 */
public class FilledValidatorSettings implements ValidatorSettings {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -6841822238596021396L;

	/**
	 * JSON name for this settings.
	 */
	public static final String JSON_SETTINGS_NAME = "validate-filled";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_ERROR_KEY_NAME = "notValidMsg-notEnough";

	/**
	 * Error key if not enough fields are filled
	 */
	private String errorKey = "";

	/**
	 * Private constructor.
	 */
	private FilledValidatorSettings() {
	}

	/**
	 * Create new instance of this class.
	 * 
	 * @return {@link FilledValidatorSettings}
	 */
	public static final FilledValidatorSettings create() {
		return new FilledValidatorSettings();
	}

	/**
	 * Set error key for not valid field value if its empty.
	 * 
	 * @param aErrorKey
	 *            - error key
	 * @return {@link FilledValidatorSettings}
	 */
	public FilledValidatorSettings setErrorKey(String aErrorKey) {
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
