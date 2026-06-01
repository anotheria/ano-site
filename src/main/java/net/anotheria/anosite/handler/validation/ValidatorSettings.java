package net.anotheria.anosite.handler.validation;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Validator settings generic interface.
 * 
 * @author Alexandr Bolbat
 */
public interface ValidatorSettings extends Serializable {

	/**
	 * Get validator settings name.
	 * 
	 * @return {@link String}
	 */
	String getName();

	/**
	 * Get validator settings as {@link JSONObject}.
	 * 
	 * @return TODO dummy comment for javadoc.
	 */
	JSONObject toJSON();

}
