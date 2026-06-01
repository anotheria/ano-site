package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Complexity validator settings.
 * 
 * @author Alexandr Bolbat
 */
public class ComplexityValidatorSettings implements ValidatorSettings {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -6841822238596021396L;

	/**
	 * JSON name for this settings.
	 */
	public static final String JSON_SETTINGS_NAME = "validate-complexity";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_REQUIRED_NUMBERS_NAME = "numbers";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_REQUIRED_UPPER_CASE_NAME = "upperCase";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_REQUIRED_LOWER_CASE_NAME = "lowerCase";

	/**
	 * JSON name for this setting.
	 */
	public static final String JSON_SETTINGS_ERROR_KEY_NAME = "notValidMsg";

	/**
	 * Require numbers.
	 */
	private boolean requiredNumbers = false;

	/**
	 * Require upper case characters.
	 */
	private boolean requiredUpperCase = false;

	/**
	 * Require lower case characters.
	 */
	private boolean requiredLowerCase = false;

	/**
	 * Error key if field value don't have required complexity.
	 */
	private String errorKey = "";

	/**
	 * Private constructor.
	 */
	private ComplexityValidatorSettings() {
	}

	/**
	 * Create new instance of this class.
	 * 
	 * @return {@link ComplexityValidatorSettings}
	 */
	public static final ComplexityValidatorSettings create() {
		return new ComplexityValidatorSettings();
	}

	/**
	 * Set numbers required for pass validation.
	 * 
	 * @param isRequired
	 *            - is required
	 * @return {@link ComplexityValidatorSettings}
	 */
	public ComplexityValidatorSettings setRequireNumbers(boolean isRequired) {
		this.requiredNumbers = isRequired;
		return this;
	}

	/**
	 * Set upper case required for pass validation.
	 * 
	 * @param isRequired
	 *            - is required
	 * @return {@link ComplexityValidatorSettings}
	 */
	public ComplexityValidatorSettings setRequireUpperCase(boolean isRequired) {
		this.requiredUpperCase = isRequired;
		return this;
	}

	/**
	 * Set lower case required for pass validation.
	 * 
	 * @param isRequired
	 *            - is required
	 * @return {@link ComplexityValidatorSettings}
	 */
	public ComplexityValidatorSettings setRequireLowerCase(boolean isRequired) {
		this.requiredLowerCase = isRequired;
		return this;
	}

	/**
	 * Set error key for not valid field value if its don't have required complexity.
	 * 
	 * @param aErrorKey
	 *            - error key
	 * @return {@link ComplexityValidatorSettings}
	 */
	public ComplexityValidatorSettings setErrorKey(String aErrorKey) {
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
			result.put(JSON_SETTINGS_REQUIRED_NUMBERS_NAME, requiredNumbers);
			result.put(JSON_SETTINGS_REQUIRED_UPPER_CASE_NAME, requiredUpperCase);
			result.put(JSON_SETTINGS_REQUIRED_LOWER_CASE_NAME, requiredLowerCase);
			result.put(JSON_SETTINGS_ERROR_KEY_NAME, errorKey);

			return result;
		} catch (JSONException e) {
			throw new RuntimeException("Preparing JSON fail.", e);
		}
	}

}
