package net.anotheria.anosite.content.variables;

/**
 * Set of constants for CalendarVariableNames enum. Due to compilation problems in 1.6 (forward declaration) moved from CalendarVariableNames in a separate class.
 * @author lrosenberg.
 */
public class CalendarVariableNamesConstants {
    /**
     * Default Date format for currentDate
     */
    public static final String DATE_DEFAULT_FORMAT = "yyyy.MM.dd  HH:mm:ss";
    /**
     * Default Date format for currentDay
     */
    public static final String DAY_DEFAULT_FORMAT = "dd";
    /**
     * Default Date format for currentMonth
     */
    public static final String MONTH_DEFAULT_FORMAT = "MMMM";
    /**
     * Default Date format for currentYear
     */
    public static final String YEAR_DEFAULT_FORMAT = "yyyy";
    /**
     * Default Date format for currentTime
     */
    public static final String TIME_DEFAULT_FORMAT = "HH:mm:ss";

    /**
     * Prevent from initialization.
     */
    private CalendarVariableNamesConstants(){;}
}
