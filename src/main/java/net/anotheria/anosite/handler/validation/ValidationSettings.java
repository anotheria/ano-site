package net.anotheria.anosite.handler.validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validation settings configuration used in {@link AbstractValidationBoxHandler}.
 * 
 * @author Alexandr Bolbat
 */
public class ValidationSettings implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 8254603089207267928L;

	/**
	 * Validation messages.
	 */
	private final Map<String, String> messages = new HashMap<String, String>();

	/**
	 * Validation forms settings.
	 */
	private final Map<String, FormSettings> settings = new HashMap<String, FormSettings>();

	/**
	 * Create new instance of {@link ValidationSettings}.
	 * 
	 * @return {@link ValidationSettings}
	 */
	public static ValidationSettings create() {
		return new ValidationSettings();
	}

	/**
	 * Private constructor.
	 */
	private ValidationSettings() {
	}

	/**
	 * Add new message to settings with some key.
	 * @param key  TODO dummy comment for javadoc.
	 * @param message  TODO dummy comment for javadoc.
	 */
	public void addMessage(String key, String message) {
		messages.put(key, message);
	}

	/**
	 * Add validator settings for some form field.
	 * 
	 * @param formId
	 *            - form id
	 * @param fieldName
	 *            - field name
	 * @param validatorSettings
	 *            - validator settings
	 */
	public void addValidatorSettings(String formId, String fieldName, ValidatorSettings validatorSettings) {
		FormSettings formSettings = settings.get(formId);
		if (formSettings == null) {
			formSettings = new FormSettings();
			settings.put(formId, formSettings);
		}

		formSettings.getFieldSettings(fieldName).addValidatorSettings(validatorSettings);
	}

	/**
	 * Set {@link String} key for valid field.
	 * 
	 * @param formId
	 *            - form id
	 * @param fieldName
	 *            - field name
	 * @param validKey
	 *            - valid key
	 */
	public void addValidKey(String formId, String fieldName, String validKey) {
		FormSettings formSettings = settings.get(formId);
		if (formSettings == null) {
			formSettings = new FormSettings();
			settings.put(formId, formSettings);
		}

		formSettings.getFieldSettings(fieldName).setValidKey(validKey);
	}

	@Override
	public final String toString() {
		try {
			return asJSON();
		} catch (JSONException e) {
			throw new RuntimeException("Preparing JSON fail.", e);
		}
	}

	/**
	 * Get settings as JSON {@link String}.
	 * 
	 * @return {@link String}
	 * @throws JSONException
	 */
	private String asJSON() throws JSONException {
		JSONObject result = new JSONObject();

		// prepare messages scope
		JSONObject messagesScope = new JSONObject();
		for (String key : messages.keySet()) {
			String message = messages.get(key);
			messagesScope.put(key, message != null ? message : "");
		}
		result.put("_messages", messagesScope);

		// prepare forms scope
		for (String formName : settings.keySet()) {
			FormSettings formSettings = settings.get(formName);
			if (formSettings == null)
				continue;

			JSONObject formJSON = new JSONObject();
			for (String fieldName : formSettings.getFieldsNames()) {
				FieldSettings fieldSettings = formSettings.getFieldSettings(fieldName);
				JSONObject fieldJSON = new JSONObject();
				fieldJSON.put("validation", fieldSettings.isValidationEnabled());
				fieldJSON.put("validMsg", fieldSettings.getValidKey());

				for (ValidatorSettings vs : fieldSettings.getValidatorsSettings())
					fieldJSON.put(vs.getName(), vs.toJSON());

				formJSON.put(fieldName, fieldJSON);
			}
			result.put(formName, formJSON);
		}

		return result.toString();
	}

	/**
	 * Form settings.
	 * 
	 * @author Alexandr Bolbat
	 */
	private class FormSettings implements Serializable {

		/**
		 * Basic serialVersionUID variable.
		 */
		private static final long serialVersionUID = 4537893175895803885L;

		/**
		 * Form fields settings.
		 */
		private Map<String, FieldSettings> fSettings = new HashMap<String, FieldSettings>();

		/**
		 * Get settings for some form field.
		 * 
		 * @param fieldName
		 *            - field name
		 * @return {@link FieldSettings}
		 */
		public FieldSettings getFieldSettings(String fieldName) {
			FieldSettings fs = fSettings.get(fieldName);
			if (fs == null) {
				fs = new FieldSettings();
				fSettings.put(fieldName, fs);
			}

			return fs;
		}

		/**
		 * Get configured fields names.
		 * 
		 * @return {@link Set} of {@link String}
		 */
		public Set<String> getFieldsNames() {
			return fSettings.keySet();
		}

	}

	/**
	 * Form field settings.
	 * 
	 * @author Alexandr Bolbat
	 */
	private class FieldSettings implements Serializable {

		/**
		 * Basic serialVersionUID variable.
		 */
		private static final long serialVersionUID = 1990525389220790133L;

		/**
		 * Field validators settings.
		 */
		private final List<ValidatorSettings> vSettings = new ArrayList<ValidatorSettings>();

		/**
		 * Valid key.
		 */
		private String validKey = "";

		/**
		 * Add validator settings.
		 * 
		 * @param vs
		 *            - {@link ValidatorSettings}
		 */
		public void addValidatorSettings(ValidatorSettings vs) {
			vSettings.add(vs);
		}

		/**
		 * Get validators settings for this field.
		 * 
		 * @return {@link List} of {@link ValidatorSettings}
		 */
		public List<ValidatorSettings> getValidatorsSettings() {
			return vSettings;
		}

		/**
		 * Set valid key.
		 * 
		 * @param aValidKey
		 *            - valid key
		 */
		public void setValidKey(String aValidKey) {
			this.validKey = aValidKey;
		}

		/**
		 * Get valid key.
		 * 
		 * @return {@link String}
		 */
		public String getValidKey() {
			return validKey;
		}

		/**
		 * Is validation enabled for this field.
		 * 
		 * @return <code>true</code> if enabled or <code>false</code>
		 */
		public boolean isValidationEnabled() {
			return !vSettings.isEmpty();
		}

	}

	public static void main(String... args) {
		ValidationSettings vs = ValidationSettings.create();
		System.out.println(vs.toString());

		vs.addMessage("msg-input1-valid", "");
		vs.addMessage("msg-input1-empty", "Empty field");
		System.out.println(vs.toString());

		// example how to use length validator settings
		ValidatorSettings s = LengthValidatorSettings.create().setMaxLength(24).setMoreThenMaxErrorKey("not-valid-key-too-long");
		vs.addValidatorSettings("sampleForm1", "sampleField1", s);
		vs.addValidKey("sampleForm1", "sampleField1", "valid-key");
		// example how to use empty validator settings
		s = EmptyValidatorSettings.create().setErrorKey("not-valid-key-empty-field-value");
		vs.addValidatorSettings("sampleForm2", "sampleField2", s);
		vs.addValidKey("sampleForm2", "sampleField2", "valid-key");
		// example how to use complexity validator settings
		s = ComplexityValidatorSettings.create().setRequireNumbers(true).setRequireLowerCase(true).setErrorKey("not-valid-key-no-complexity");
		vs.addValidatorSettings("sampleForm2", "samplePasswordField2", s);
		vs.addValidKey("sampleForm2", "samplePasswordField2", "valid-password-key");

		System.out.println(vs.toString());
	}

}
