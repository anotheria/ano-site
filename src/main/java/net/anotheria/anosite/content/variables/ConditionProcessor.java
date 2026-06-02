package net.anotheria.anosite.content.variables;

import net.anotheria.util.content.template.processors.variables.ConditionProcessorNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

public class ConditionProcessor implements VariablesProcessor {
	private final Logger log = LoggerFactory.getLogger(ConditionProcessor.class);

	@Override
	public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
		//sorry!  next If block -- is just the stupid hack!  due  to 'if' prefix name (
		if (ConditionProcessorNames.iF.getPrefixName().equals(prefix)) {
			return ConditionProcessorNames.iF.executeReplacement(variable, defValue);
		} else {
			try {
				return ConditionProcessorNames.valueOf(ConditionProcessorNames.class, prefix).executeReplacement(variable, defValue);
			} catch (Exception ignored) {
				log.error("An exceptions has been occurred while trying to replace variable. where prefix=" + prefix + " variable=" + variable + " defaultvalue=" + defValue, ignored);
			}
		}
		return "";
	}

}
