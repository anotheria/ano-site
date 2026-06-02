package net.anotheria.anosite.content.variables;

import jakarta.servlet.http.HttpServletRequest;

public class ParameterProcessor implements VariablesProcessor {

	public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
		String val = req.getParameter(variable);
		return val == null || val.length()==0 ?
				defValue : val;
	}
	
}
