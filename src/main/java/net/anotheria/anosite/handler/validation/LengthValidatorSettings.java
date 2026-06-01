package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Length validator settings.
 * 
 * @author Alexandr Bolbat
 */
public class LengthValidatorSettings implements ValidatorSettings {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -6841822238596021396L;

	/**
	 * JSON name for this settings.
	 */
	public static final String JSON_SETTINGS_NAME = "validate-length";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_MIN_NAME = "min";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_MAX_NAME = "max";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_LESS_THEN_MIN_ERROR_KEY_NAME = "notValidMsg-tooShort";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_MORE_THEN_MAX_ERROR_KEY_NAME = "notValidMsg-tooLong";

	/**
	 * Minimum length.
	 */
	private int min = 0;

	/**
	 * Maximum length.
	 */
	private int max = Integer.MAX_VALUE;

	/**
	 * Error key if length less then possible minimum.
	 */
	private String lessThenMinErrorKey = "";

	/**
	 * Error key if length more then possible maximum.
	 */
	private String moreThenMaxErrorKey = "";

	/**
	 * Private constructor.
	 */
	private LengthValidatorSettings() {
	}

	/**
	 * Create new instance of this class.
	 * 
	 * @return {@link LengthValidatorSettings}
	 */
	public static final LengthValidatorSettings create() {
		return new LengthValidatorSettings();
	}

	/**
	 * Set minimum length size.
	 * 
	 * @param minLength
	 *            - minimum length
	 * @return {@link LengthValidatorSettings}
	 */
	public LengthValidatorSettings setMinLength(int minLength) {
		this.min = minLength;
		return this;
	}

	/**
	 * Set maximum length size.
	 * 
	 * @param maxLength
	 *            - maximum length
	 * @return {@link LengthValidatorSettings}
	 */
	public LengthValidatorSettings setMaxLength(int maxLength) {
		this.max = maxLength;
		return this;
	}

	/**
	 * Set error key for not valid field value if its length less then possible minimum.
	 * 
	 * @param errorKey
	 *            - error key
	 * @return {@link LengthValidatorSettings}
	 */
	public LengthValidatorSettings setLessThenMinErrorKey(String errorKey) {
		this.lessThenMinErrorKey = errorKey;
		return this;
	}

	/**
	 * Set error key for not valid field value if its length more then possible maximum.
	 * 
	 * @param errorKey
	 *            - error key
	 * @return {@link LengthValidatorSettings}
	 */
	public LengthValidatorSettings setMoreThenMaxErrorKey(String errorKey) {
		this.moreThenMaxErrorKey = errorKey;
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
			result.put(JSON_SETTINGS_MIN_NAME, min);
			result.put(JSON_SETTINGS_MAX_NAME, max);
			result.put(JSON_SETTINGS_LESS_THEN_MIN_ERROR_KEY_NAME, lessThenMinErrorKey);
			result.put(JSON_SETTINGS_MORE_THEN_MAX_ERROR_KEY_NAME, moreThenMaxErrorKey);

			return result;
		} catch (JSONException e) {
			throw new RuntimeException("Preparing JSON fail.", e);
		}
	}

}
