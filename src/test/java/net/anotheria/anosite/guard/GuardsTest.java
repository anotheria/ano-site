package net.anotheria.anosite.guard;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.ArrayList;

import net.anotheria.anodoc.util.context.CallContext;
import net.anotheria.anodoc.util.context.CallContextFactory;
import net.anotheria.anodoc.util.context.ContextManager;

import org.junit.BeforeClass;
import org.junit.Test;

public class GuardsTest {
	@BeforeClass public static void init(){
		ContextManager.setFactory(new TestCallContextFactory());
	}
	
	@Test public void testContextLanguageGuards() throws Exception{
		ContextLanguageIsGuard de = new ContextLanguageIsGermanGuard();
		ContextLanguageIsGuard en = new ContextLanguageIsEnglishGuard();
		ContextLanguageIsGuard ru = new ContextLanguageIsRussianGuard();
		
		ContextManager.getCallContext().setCurrentLanguage("DE");
		testContextLanguageGuardPositive(de);
		testContextLanguageGuardNegative(en,ru);
	}
	
	private void testContextLanguageGuardPositive(ContextLanguageIsGuard... guards ) throws Exception{
		testContextLanguageGuard(true, guards);
	}
	private void testContextLanguageGuardNegative(ContextLanguageIsGuard... guards ) throws Exception{
		testContextLanguageGuard(false, guards);
	}

	private void testContextLanguageGuard(boolean expected, ContextLanguageIsGuard... guards ) throws Exception{
		for (ContextLanguageIsGuard g : guards){
			if (g.isConditionFullfilled(null, null) != expected){
				fail("Expected "+expected+" for guard "+g);
			}
		}
	}
	
	private static class TestCallContextFactory implements CallContextFactory{

		@Override
		public CallContext createContext() {
			return new CallContext(){
				@Override
				public String getDefaultLanguage() {
					return "EN";
				}
				public List<String> getSupportedLanguages() {
					List<String> ret = new ArrayList<String>();
					ret.add("EN");
					ret.add("DE");
					return ret;
				}
			};
		}
		
	}

}
