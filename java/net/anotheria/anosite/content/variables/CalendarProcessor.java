package net.anotheria.anosite.content.variables;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Variable processor for dates.
 * 
 * @author h3llka
 */
public class CalendarProcessor implements VariablesProcessor {
	/**
	 * Logger.
	 */
    private static final Logger log = LoggerFactory.getLogger(CalendarProcessor.class);

    
    /**
     * Returns variable value for incoming variable parameter. If format - which represented by <a>defValue</a> - valid,
     * then variable value will be represented in  current format, else default format will be used.
     * <a>@see net.anotheria.anosite.content.variables.CalendarVariableNames).</a>
     * When incoming variable does not exists, or wrong -  <a>Wrong or unsupported variable</a> will be returned as well.
     * <a>Variable can't be null</a> will be  returned  when variable - null.
     *
     * @param prefix Processor prefix
     * @param variable variable name
     * @param defValue currently string representation of dateFormat
     * @param req request instance
     * @return variable value
     */
    @Override public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
        try {
            CalendarVariableNames variableName = CalendarVariableNames.valueOf(CalendarVariableNames.class, variable);
            return variableName.getVariableValue(defValue);
        }
        catch (NullPointerException e){
            log.error("Exception has been occured while trying to use CallendarProcessor replace method", e);
            log.debug("incoming variable = " + variable);
             return "Variable can't be null";
        }
        catch (IllegalArgumentException e) {
            log.error("Exception has been occured while trying to use CallendarProcessor replace method", e);
            log.debug("incoming variable = " + variable);
            return "Wrong or unsupported variable";
        }
    }
}

