package net.anotheria.anosite.content.variables;

import static net.anotheria.anosite.content.variables.VariablesUtility.replaceVariables;

import jakarta.servlet.http.HttpServletRequest;

import junit.framework.Assert;
import net.anotheria.anoprise.metafactory.Extension;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.asresourcedata.service.fixture.ASResourceDataServiceFixtureFactory;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Junit for varUtilities.
 *
 * @author h3llka
 */
public class VariablesUtilityTest {
	
	@BeforeClass public static void init(){
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
		Assert.assertNotNull("Should not be null", testReplaceVAR1);
		Assert.assertEquals("Should not be null", "I'm text for replace! " + SimpleTestProcessor.var1Value, testReplaceVAR1);

		String testReplaceToDefault = replaceVariables(null, text2);
		Assert.assertNotNull("Should not be null", testReplaceToDefault);
		Assert.assertEquals("Should not be null", "TEST 2 " + "default___", testReplaceToDefault);

		String testReplaceStaticContent = replaceVariables(null, staticText);
		Assert.assertNotNull("Should not be null", testReplaceStaticContent);
		Assert.assertEquals("Should not be null", staticText, testReplaceStaticContent);

		String replaceEmpty = replaceVariables(null, "");
		Assert.assertTrue("Should be empty", replaceEmpty.isEmpty());
	}

	@Test
	public void processorOperations() {
		VariablesUtility.addProcessor(SimpleTestProcessor.PREFIX, new SimpleTestProcessor());
		Assert.assertNotNull(VariablesUtility.getDefaultProcessors().get(SimpleTestProcessor.PREFIX));
		Assert.assertTrue(VariablesUtility.getDefaultProcessors().get(SimpleTestProcessor.PREFIX) instanceof SimpleTestProcessor);
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
