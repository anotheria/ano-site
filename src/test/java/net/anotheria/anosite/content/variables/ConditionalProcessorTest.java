package net.anotheria.anosite.content.variables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import net.anotheria.util.content.template.processors.variables.ConditionProcessorNames;

/**
 * @author: h3llka
 */
public class ConditionalProcessorTest {

    private ConditionProcessor processor;

    @BeforeEach

    public void setUp() {
        processor = new ConditionProcessor();
    }

    @Test

    public void testSipleFlow() {

        String valueIf = processor.replace(ConditionProcessorNames.iF.getPrefixName(), "true", "1;", null);
        assertTrue(valueIf.equals("1"), "should be true");

        String valueNotIf = processor.replace(ConditionProcessorNames.ifNot.getPrefixName(), "false", "1;", null);
        assertTrue(valueNotIf.equals("1;"), "should be true");

        String valuePrS = processor.replace(ConditionProcessorNames.present.getPrefixName(), "yeap", "test", null);
        assertTrue(valuePrS.equals("test"), "shpud be true");

        String valueNotPrS = processor.replace(ConditionProcessorNames.notPresent.getPrefixName(), "", "test", null);
        assertTrue(valueNotPrS.equals("test"), "shpud be true");

        String valueEquals = processor.replace(ConditionProcessorNames.equals.getPrefixName(), "", "", null);
        assertTrue(Boolean.valueOf(valueEquals), "shpuld be true");

        String valueNotEquals = processor.replace(ConditionProcessorNames.notEquals.getPrefixName(), "", "", null);
        assertTrue(!Boolean.valueOf(valueNotEquals), "shpuld be true");


        String valueInRange1 = processor.replace(ConditionProcessorNames.inRange.getPrefixName(), "10-15", "11", null);
        assertTrue(Boolean.valueOf(valueInRange1), "shpuld be true");

        String valueInRange2 = processor.replace(ConditionProcessorNames.inRange.getPrefixName(), "10-15", "0", null);
        assertTrue(!Boolean.valueOf(valueInRange2), "shpuld be true");


        String valGreaterThan = processor.replace(ConditionProcessorNames.greaterThan.getPrefixName(), "10", "1", null);
        assertTrue(Boolean.valueOf(valGreaterThan), "shpuld be true");
        String valGreaterEq1 = processor.replace(ConditionProcessorNames.greaterEqual.getPrefixName(), "10", "1", null);
        assertTrue(Boolean.valueOf(valGreaterEq1), "shpuld be true");
        String valGreaterEq2 = processor.replace(ConditionProcessorNames.greaterEqual.getPrefixName(), "10", "10", null);
        assertTrue(Boolean.valueOf(valGreaterEq2), "shpuld be true");

        String valeLessThan = processor.replace(ConditionProcessorNames.lessThan.getPrefixName(), "1", "100", null);
        assertTrue(Boolean.valueOf(valeLessThan), "shpuld be true");
        String valeLessEq1 = processor.replace(ConditionProcessorNames.lessEqual.getPrefixName(), "1", "100", null);
        assertTrue(Boolean.valueOf(valeLessEq1), "shpuld be true");
        String valeLessEq2 = processor.replace(ConditionProcessorNames.lessEqual.getPrefixName(), "100", "100", null);
        assertTrue(Boolean.valueOf(valeLessEq2), "shpuld be true");
    }

    /**
     * trying to post bad data.
     */
    @Test
    public void testWrongUse() {
        String reS = processor.replace("", ",", "", null);
        assertTrue(reS.isEmpty());
        processor.replace(null, ",", "", null);
    }
}
