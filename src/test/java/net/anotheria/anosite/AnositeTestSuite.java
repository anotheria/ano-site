package net.anotheria.anosite;


import net.anotheria.anosite.content.variables.CalendarProcessorTestCase;
import net.anotheria.anosite.content.variables.ConditionalProcessorTest;
import net.anotheria.anosite.content.variables.TextResourceProcessorTestCase;
import net.anotheria.anosite.content.variables.VariablesUtilityTest;
import net.anotheria.anosite.guard.GuardsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {CalendarProcessorTestCase.class, TextResourceProcessorTestCase.class, GuardsTest.class, ConditionalProcessorTest.class,
		VariablesUtilityTest.class
})
public class AnositeTestSuite {

}
