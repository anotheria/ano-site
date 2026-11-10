package net.anotheria.anosite.content.variables;

import static net.anotheria.anosite.content.variables.VariablesUtility.replaceVariables;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import net.anotheria.anoprise.metafactory.Extension;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.asresourcedata.service.fixture.ASResourceDataServiceFixtureFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Junit for varUtilities.
 *
 * @author h3llka
 */
public class VariablesUtilityTest {
	
	@BeforeAll public static void init(){
		MetaFactory.addFactoryClass(IASResourceDataService.class, Extension.NONE, ASResourceDataServiceFixtureFactory.class);
	}
	

	@Test
	public void replaceTest() {
		VariablesUtility.addProcessor(SimpleTestProcessor.PREFIX, new SimpleTestProcessor());
		String replacePart1 = '{' + SimpleTestProcessor.PREFIX + ":" + SimpleTestProcessor.var1 + "}";
		String replacePart2 = '{' + SimpleTestProcessor.PREFIX + ":not_exist:default___}";
		String text = "I'm text for replace! " + replacePart1;
		String text2 = "TEST 2 " + replacePart2;
		String staticText = "I'm static {::::>>>>>}}}}}";

		String testReplaceVAR1 = replaceVariables(null, text);
		Assertions.assertNotNull(testReplaceVAR1, "Should not be null");
		Assertions.assertEquals("I'm text for replace! " + SimpleTestProcessor.var1Value, testReplaceVAR1, "Should not be null");

		String testReplaceToDefault = replaceVariables(null, text2);
		Assertions.assertNotNull(testReplaceToDefault, "Should not be null");
		Assertions.assertEquals("TEST 2 " + "default___", testReplaceToDefault, "Should not be null");

		String testReplaceStaticContent = replaceVariables(null, staticText);
		Assertions.assertNotNull(testReplaceStaticContent, "Should not be null");
		Assertions.assertEquals(staticText, testReplaceStaticContent, "Should not be null");

		String replaceEmpty = replaceVariables(null, "");
		Assertions.assertTrue(replaceEmpty.isEmpty(), "Should be empty");
	}

	@Test
	public void processorOperations() {
		VariablesUtility.addProcessor(SimpleTestProcessor.PREFIX, new SimpleTestProcessor());
		Assertions.assertNotNull(VariablesUtility.getDefaultProcessors().get(SimpleTestProcessor.PREFIX));
		Assertions.assertTrue(VariablesUtility.getDefaultProcessors().get(SimpleTestProcessor.PREFIX) instanceof SimpleTestProcessor);
	}

	/**
	 * VariablesProcessor.
	 */
	private static class SimpleTestProcessor implements VariablesProcessor {
		/**
		 * Prefix.
		 */
		public static String PREFIX = "testProcessor!!!";
		/**
		 * Test var.
		 */
		public static String var1 = "test1";
		/**
		 * Hardcoded ret value.
		 */
		public static String var1Value = "checked_";

		@Override
		public String replace(String aPrefix, String aVariable, String aDefValue, HttpServletRequest aContext) {
			if (aVariable.equals(var1))
				return var1Value;
			else return aDefValue;

		}
	}

}
