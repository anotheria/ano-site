package net.anotheria.anosite.decorator;

import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Decorator to show created/updated date of post/comment in usual format instead of long type number.
 *
 * @author vbezuhlyi
 */
public class DateDecorator implements IAttributeDecorator {

    /**
     * Default format of date/time for this decorator.
     */
    private static String DEFAULT_DATE_FORMAT = "dd.MM.yyyy HH:mm";


    @Override
    public String decorate(DataObject doc, String attributeName, String rule) {
        if (StringUtils.isEmpty(rule) || rule.equals("null")) { // yes, ShowPostAction sends parameter "null" as String by default
            rule = DEFAULT_DATE_FORMAT;
        }

        return "Incompatible element for decoration";
    }

    private String formatDate(long time, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);
        return dateFormat.format(date);
    }
}
