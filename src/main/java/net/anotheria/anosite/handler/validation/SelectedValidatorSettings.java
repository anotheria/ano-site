package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Selected validator settings.
 * 
 * @author Alexandr Bolbat
 */
public class SelectedValidatorSettings implements ValidatorSettings {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 6107515800456281268L;

	/**
	 * JSON name for this settings.
	 */
	public static final String JSON_SETTINGS_NAME = "validate-selected";

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
	private SelectedValidatorSettings() {
	}

	/**
	 * Create new instance of this class.
	 * 
	 * @return {@link SelectedValidatorSettings}
	 */
	public static final SelectedValidatorSettings create() {
		return new SelectedValidatorSettings();
	}

	/**
	 * Set error key for not valid field value if its empty.
	 * 
	 * @param aErrorKey
	 *            - error key
	 * @return {@link SelectedValidatorSettings}
	 */
	public SelectedValidatorSettings setErrorKey(String aErrorKey) {
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
