package net.anotheria.anosite.guard;

/**
 * This guard is fulfilled if the current context language is set to DE.
 * @author lrosenberg
 *
 */
public class ContextLanguageIsGermanGuard extends ContextLanguageIsGuard{

	@Override
	protected String getDesiredContextLanguage() {
		return "DE";
	}
	
}
