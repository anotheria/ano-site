package net.anotheria.anosite.handler.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anotheria.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Validation response container used in {@link AbstractValidationBoxHandler}.
 * 
 * @author Alexandr Bolbat
 */
public class ValidationResponse implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -4130382436892907495L;

	/**
	 * Empty validation response.
	 */
	public static final ValidationResponse EMPTY_RESPONSE = new ValidationResponse("empty-response");

	/**
	 * Fields data.
	 */
	private final Map<String, FieldData> fieldsData = new HashMap<String, FieldData>();

	/**
	 * Form id.
	 */
	private final String formId;

	/**
	 * Create new instance of {@link ValidationResponse}.
	 * 
	 * @param aFormId
	 *            - form id
	 * @return {@link ValidationResponse}
	 */
	public static ValidationResponse create(final String aFormId) {
		return new ValidationResponse(aFormId);
	}

	/**
	 * Private constructor.
	 * 
	 * @param aFormId
	 *            - form id
	 */
	private ValidationResponse(final String aFormId) {
		if (StringUtils.isEmpty(aFormId))
			throw new IllegalArgumentException("empty form id");

		this.formId = aFormId;
	}

	/**
	 * Add field with error key to response.
	 * 
	 * @param fieldName
	 *            - field name
	 * @param errorKey
	 *            - error key
	 */
	public void addError(final String fieldName, final String errorKey) {
		FieldData fData = fieldsData.get(fieldName);
		if (fData == null) {
			fData = new FieldData();
			fieldsData.put(fieldName, fData);
		}

		fData.addErrorKey(errorKey);
	}

	/**
	 * Is validation response has errors.
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	public boolean hasErrors() {
		return !fieldsData.isEmpty();
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
	 * Get response as JSON {@link String}.
	 * 
	 * @return {@link String}
	 * @throws JSONException
	 */
	private String asJSON() throws JSONException {
		JSONObject result = new JSONObject();

		// prepare status
		result.put("status", (hasErrors() ? "ERROR" : "OK"));
		result.put("formId", formId);

		// prepare errors
		if (hasErrors()) {
			JSONObject errorsScope = new JSONObject();

			// prepare errors
			for (String fieldName : fieldsData.keySet())
				errorsScope.put(fieldName, new JSONArray(fieldsData.get(fieldName).getErrorsKeys()));

			result.put("errors", errorsScope);
		}

		return result.toString();
	}

	/**
	 * Class containing information about not valid field value.
	 * 
	 * @author Alexandr Bolbat
	 */
	private final class FieldData {

		/**
		 * Error keys.
		 */
		private final List<String> errorsKeys;

		/**
		 * Constructor.
		 */
		public FieldData() {
			this.errorsKeys = new ArrayList<String>();
		}

		public List<String> getErrorsKeys() {
			return errorsKeys;
		}

		/**
		 * Add error key.
		 * 
		 * @param errorKey
		 *            - error key
		 */
		public void addErrorKey(String errorKey) {
			errorsKeys.add(errorKey);
		}

	}

	public static void main(String... args) {
		ValidationResponse vr = ValidationResponse.create("testForm");
		System.out.println(vr.toString());
		vr.addError("testField1", "error-key");
		vr.addError("testField2", "error-key-1");
		vr.addError("testField2", "error-key-2");
		System.out.println(vr.toString());
	}

}
