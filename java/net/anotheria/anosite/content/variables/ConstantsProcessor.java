package net.anotheria.anosite.content.variables;

import net.anotheria.util.StringUtils;
import net.anotheria.util.content.template.processors.variables.ConstantVariables;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Variables processor for constants. Useful for html tags with colide with variables processor syntax for example &amp;nbsp; etc.
 *
 * @author another
 */
public class ConstantsProcessor implements VariablesProcessor {

	/**
	 * Prefix.
	 */
	public static final String PREFIX = DefinitionPrefixes.PREFIX_CONSTANT;


	@Override
	public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
		if (PREFIX.equals(prefix) && !StringUtils.isEmpty(variable)) {
			String value = ConstantVariables.getConstantValue(variable);
			return value != null ? value : "Unknown constants: " + variable;
		}
		return defValue;
	}
}
