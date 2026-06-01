package net.anotheria.anosite.content.variables;

import junit.framework.TestCase;
import net.anotheria.anoplass.api.APICallContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * @author: h3llka
 */
public class CalendarProcessorTestCase extends TestCase {
    private CalendarProcessor processor;
    private final Logger log = LoggerFactory.getLogger(CalendarProcessorTestCase.class);

    public void setUp() throws Exception {
        processor = new CalendarProcessor();
    }

    /**
     * Simplest flow test! - With default Formater
     */
    public void testSimpleFlow() {
        String currentDate = processor.replace("", "currentDate", "", null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        String currentMonth = processor.replace("", "currentMonth", "", null);
        assertNotNull("should not be null", currentMonth);
        log.debug("Month = " + currentMonth);
        String currentYear = processor.replace("", "currentYear", "", null);
        assertNotNull("should not be null", currentYear);
        log.debug("Year = " + currentYear);
        String currentTime = processor.replace("", "currentTime", "", null);
        assertNotNull("should not be null", currentTime);
        log.debug("Time = " + currentTime);

        assertNotNull("should not be null", processor.replace("", "anyThing", "", null));
        assertNotNull("should be null", processor.replace("", "", null, null));
        assertEquals("should be null", processor.replace("", "", null, null),"Wrong or unsupported variable");

    }

    /**
     * Current test checks CalendarVariableNames.currentMonth - with different formats
     */
    public void testMonthWithFormats() throws Exception{
        //Checking Month in diff Formats
        SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentMonth.getVariableName(), "MMMMM", null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentMonth.getVariableName(), "MMM", null);
        //trying to createSuchDate!
        format=new SimpleDateFormat("MMM", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull("should not be null", currentDate);
        assertNotNull("should not be null", format.parse(currentDate));
        assertEquals("Should be same", currentDate,format.format(format.parse(currentDate)));

        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentMonth.getVariableName(), "I'm really Wrong Format", null);
        assertNotNull("should not be null", currentDateWrongFormat);
        assertEquals("should be same as default Format", currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentMonth.getVariableName(),CalendarVariableNames.currentMonth.getDefaultFormatPattern(), null));
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);


    }

    /**
     * Current test checks CalendarVariableNames.currentDate - with different formats
     */
    public void testDateWithFormats() throws Exception{
         SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentDate.getVariableName(), "dd/MM/yy, Z", null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentDate.getVariableName(),"dd/MM/yy HH:mm:ss", null);
        assertNotNull("should not be null", currentDate);
        format=new SimpleDateFormat("dd/MM/yy HH:mm:ss", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull("should not be null", format.parse(currentDate));
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentDate.getVariableName(),"I'm really Wrong Format", null);
        assertNotNull("should not be null", currentDateWrongFormat);
        assertEquals("should be same as default Format", currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentDate.getVariableName(),
             CalendarVariableNames.currentDate.getDefaultFormatPattern(),  null));
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * Same as Previous but with day
     */
    public void testDayWithFormats() throws Exception{
         SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentDay.getVariableName(),"d", null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentDay.getVariableName(), "dd", null);
        assertNotNull("should not be null", currentDate);
        format=new SimpleDateFormat("dd", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull("should not be null",format.parse(currentDate));
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentDay.getVariableName(), ":I'm really Wrong Format", null);
        assertNotNull("should not be null", currentDateWrongFormat);
        assertEquals("should be same as default Format", currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentDay.getVariableName(),
                CalendarVariableNames.currentDay.getDefaultFormatPattern(), null));
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * same as previous but for year
     */
    public void testYearWithFormats() throws Exception{
         SimpleDateFormat format;
        String currentDate = processor.replace("", CalendarVariableNames.currentYear.getVariableName(), "yyyy", null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentYear.getVariableName(), "yy",null);
        assertNotNull("should not be null", currentDate);
        format=new SimpleDateFormat("yy", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull("should not be null",format.parse(currentDate));
        log.debug("Date = " + currentDate);
        // System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentYear.getVariableName(),"y", null);
        assertNotNull("should not be null", currentDate);
        format=new SimpleDateFormat("y", APICallContext.getCallContext().getCurrentLocale());
        assertNotNull("should not be null",format.parse(currentDate));
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentYear.getVariableName() , ":I'm really Wrong Format", null);
        assertNotNull("should not be null", currentDateWrongFormat);
        assertEquals("should be same as default Format", currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentYear.getVariableName(),
                CalendarVariableNames.currentYear.getDefaultFormatPattern(),  null));
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * Same as previous but for Time
     */
    public void testTimeWithFormats() {
        String currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), "HH:mm" , null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), "HH:mm:ss, Z",  null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), "h:mm a",  null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        currentDate = processor.replace("", CalendarVariableNames.currentTime.getVariableName() , "hh:mm a, Z", null);
        assertNotNull("should not be null", currentDate);
        log.debug("Date = " + currentDate);
        //System.out.println(currentDate);

        //Here Default Format should be used -  as  Format is Wrong
        String currentDateWrongFormat = processor.replace("", CalendarVariableNames.currentTime.getVariableName(), ":I'm really Wrong Format", null);
        assertNotNull("should not be null", currentDateWrongFormat);
        assertEquals("should be same as default Format", currentDateWrongFormat, processor.replace("", CalendarVariableNames.currentTime.getVariableName(),
                CalendarVariableNames.currentTime.getDefaultFormatPattern(), null));
        log.debug("Date = " + currentDateWrongFormat);
        //System.out.println(currentDateWrongFormat);

    }

    /**
     * checking different simple ways to crash all :)
     */
    public void testWrongIncomingParams() {

        //IllegalArgumentException
        String result = "Wrong or unsupported variable";
        String case1 = processor.replace("", "I'm does not exists : not format", result, null);
        assertNotNull("Should be Not Null!!!", case1);
        assertEquals("Should be same!!!", case1, result);
        //Nupex
        String case2 = processor.replace("", null, result, null);
        assertNotNull("Should be Not Null!!!", case2);
        assertEquals("Should be same!!!", case2, "Variable can't be null");
        //IllegalArgumentException -- empty str
        String case3 = processor.replace("", "", null, null);
        assertNotNull("Should be Not Null!!!", case3);
        assertEquals("Should be same!!!", case3, result);
        //Wrong Variable Value
        String case4 = processor.replace("", ":", result, null);
        assertNotNull("Should be Not Null!!!", case4);
        assertEquals("Should be same!!!", case4, result);

    }


}
