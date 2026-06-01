package net.anotheria.anosite.guard;

/**
 * This guard is fulfilled if the current context language is set to EN.
 * @author lrosenberg
 *
 */
public class ContextLanguageIsEnglishGuard extends ContextLanguageIsGuard{

	@Override
	protected String getDesiredContextLanguage() {
		return "EN";
	}
	
}
