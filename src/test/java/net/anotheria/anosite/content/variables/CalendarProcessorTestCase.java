package net.anotheria.anosite.content.variables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import net.anotheria.anoplass.api.APICallContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * @author: h3llka
 */
public class CalendarProcessorTestCase {
    private CalendarProcessor processor;
    private final Logger log = LoggerFactory.getLogger(CalendarProcessorTestCase.class);

    @BeforeEach

    public void setUp() throws Exception {
        processor = new CalendarProcessor();
    }

    /**
     * Simplest flow test! - With default Formater
     */
    @Test
    public void testSimpleFlow() {
        String currentDate = processor.replace("", "currentDate", "", null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        String currentMonth = processor.replace("", "currentMonth", "", null);
        assertNotNull(currentMonth, "should not be null");
        log.debug("Month = " + currentMonth);
        String currentYear = processor.replace("", "currentYear", "", null);
        assertNotNull(currentYear, "should not be null");
        log.debug("Year = " + currentYear);
        String currentTime = processor.replace("", "currentTime", "", null);
        assertNotNull(currentTime, "should not be null");
        log.debug("Time = " + currentTime);

        assertNotNull(processor.replace("", "anyThing", "", null), "should not be null");
        assertNotNull(processor.replace("", "", null, null), "should be null");
        assertEquals(processor.replace("", "", null, null), "Wrong or unsupported variable", "should be null");

    }

    /**
     * Current test checks CalendarVariableNames.currentMonth - with different formats
     */
    @Test
    public void testMonthWithFormats() throws Exception{
        //Checking Month in diff Formats
        SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentMonth.getVariableName(), "MMMMM", null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentMonth.getVariableName(), "MMM", null);
        //trying to createSuchDate!
        format=new SimpleDateFormat("MMM", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull(currentDate, "should not be null");
        assertNotNull(format.parse(currentDate), "should not be null");
        assertEquals(currentDate, format.format(format.parse(currentDate)), "Should be same");

        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentMonth.getVariableName(), "I'm really Wrong Format", null);
        assertNotNull(currentDateWrongFormat, "should not be null");
        assertEquals(currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentMonth.getVariableName(),CalendarVariableNames.currentMonth.getDefaultFormatPattern(), null), "should be same as default Format");
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);


    }

    /**
     * Current test checks CalendarVariableNames.currentDate - with different formats
     */
    @Test
    public void testDateWithFormats() throws Exception{
         SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentDate.getVariableName(), "dd/MM/yy, Z", null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentDate.getVariableName(),"dd/MM/yy HH:mm:ss", null);
        assertNotNull(currentDate, "should not be null");
        format=new SimpleDateFormat("dd/MM/yy HH:mm:ss", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull(format.parse(currentDate), "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentDate.getVariableName(),"I'm really Wrong Format", null);
        assertNotNull(currentDateWrongFormat, "should not be null");
        assertEquals(currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentDate.getVariableName(),
             CalendarVariableNames.currentDate.getDefaultFormatPattern(),  null), "should be same as default Format");
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * Same as Previous but with day
     */
    @Test
    public void testDayWithFormats() throws Exception{
         SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentDay.getVariableName(),"d", null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentDay.getVariableName(), "dd", null);
        assertNotNull(currentDate, "should not be null");
        format=new SimpleDateFormat("dd", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull(format.parse(currentDate), "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentDay.getVariableName(), ":I'm really Wrong Format", null);
        assertNotNull(currentDateWrongFormat, "should not be null");
        assertEquals(currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentDay.getVariableName(),
                CalendarVariableNames.currentDay.getDefaultFormatPattern(), null), "should be same as default Format");
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * same as previous but for year
     */
    @Test
    public void testYearWithFormats() throws Exception{
         SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentYear.getVariableName(), "yyyy", null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentYear.getVariableName(), "yy",null);
        assertNotNull(currentDate, "should not be null");
        format=new SimpleDateFormat("yy", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull(format.parse(currentDate), "should not be null");
        log.debug("Date = " + currentDate);
        // System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentYear.getVariableName(),"y", null);
        assertNotNull(currentDate, "should not be null");
        format=new SimpleDateFormat("y", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull(format.parse(currentDate), "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentYear.getVariableName() , ":I'm really Wrong Format", null);
        assertNotNull(currentDateWrongFormat, "should not be null");
        assertEquals(currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentYear.getVariableName(),
                CalendarVariableNames.currentYear.getDefaultFormatPattern(),  null), "should be same as default Format");
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * Same as previous but for Time
     */
    @Test
    public void testTimeWithFormats() {
        String currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), "HH:mm" , null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), "HH:mm:ss, Z",  null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), "h:mm a",  null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName() , "hh:mm a, Z", null);
        assertNotNull(currentDate, "should not be null");
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), ":I'm really Wrong Format", null);
        assertNotNull(currentDateWrongFormat, "should not be null");
        assertEquals(currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentTime.getVariableName(),
                CalendarVariableNames.currentTime.getDefaultFormatPattern(), null), "should be same as default Format");
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * checking different simple ways to crash all :)
     */
    @Test
    public void testWrongIncomingParams() {

        //IllegalArgumentException
        String result = "Wrong or unsupported variable";
        String case1 = processor.replace("", "I'm does not exists : not format", result, null);
        assertNotNull(case1, "Should be Not Null!!!");
        assertEquals(case1, result, "Should be same!!!");
        //Nupex
        String case2 = processor.replace("", null, result, null);
        assertNotNull(case2, "Should be Not Null!!!");
        assertEquals(case2, "Variable can't be null", "Should be same!!!");
        //IllegalArgumentException -- empty str
        String case3 = processor.replace("", "", null, null);
        assertNotNull(case3, "Should be Not Null!!!");
        assertEquals(case3, result, "Should be same!!!");
        //Wrong Variable Value
        String case4 = processor.replace("", ":", result, null);
        assertNotNull(case4, "Should be Not Null!!!");
        assertEquals(case4, result, "Should be same!!!");

    }


}
