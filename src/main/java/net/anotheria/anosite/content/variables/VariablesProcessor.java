package net.anotheria.anosite.content.variables;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Text replace processor interface.
 * 
 * @author denis
 */
public interface VariablesProcessor {
	/**
	 * Replace method.
	 * @param prefix  processor prefix
	 * @param variable variable to replace
	 * @param defValue default value
	 * @param req request
	 * @return replaced text
	 */
	String replace(String prefix, String variable, String defValue, HttpServletRequest req);
}
