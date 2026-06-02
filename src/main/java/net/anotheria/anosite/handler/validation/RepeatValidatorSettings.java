package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Repeat validator settings. For example can be used for configure password confirm validation.
 * 
 * @author Alexandr Bolbat
 */
public class RepeatValidatorSettings implements ValidatorSettings {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -6841822238596021396L;

	/**
	 * JSON name for this settings.
	 */
	public static final String JSON_SETTINGS_NAME = "validate-repeat";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_FIELD_NAME = "fieldName";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_ERROR_KEY_NAME = "notValidMsg";

	/**
	 * Field name.
	 */
	private String fieldName = "";

	/**
	 * Error key.
	 */
	private String errorKey = "";

	/**
	 * Private constructor.
	 */
	private RepeatValidatorSettings() {
	}

	/**
	 * Create new instance of this class.
	 * 
	 * @return {@link RepeatValidatorSettings}
	 */
	public static final RepeatValidatorSettings create() {
		return new RepeatValidatorSettings();
	}

	/**
	 * Set repeat field name with what we need check current field value.
	 * 
	 * @param aFieldName
	 *            - set is need validate email format
	 * @return {@link RepeatValidatorSettings}
	 */
	public RepeatValidatorSettings setRepeatField(final String aFieldName) {
		this.fieldName = aFieldName;
		return this;
	}

	/**
	 * Set error key for not valid field value.
	 * 
	 * @param aErrorKey
	 *            - error key
	 * @return {@link RepeatValidatorSettings}
	 */
	public RepeatValidatorSettings setErrorKey(String aErrorKey) {
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
			result.put(JSON_SETTINGS_FIELD_NAME, fieldName);
			result.put(JSON_SETTINGS_ERROR_KEY_NAME, errorKey);

			return result;
		} catch (JSONException e) {
			throw new RuntimeException("Preparing JSON fail.", e);
		}
	}

}
