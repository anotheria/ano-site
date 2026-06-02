package net.anotheria.anosite.guard;

/**
 * This guard is fulfilled if the current context language is set to RU.
 * @author lrosenberg
 *
 */
public class ContextLanguageIsRussianGuard extends ContextLanguageIsGuard{

	@Override
	protected String getDesiredContextLanguage() {
		return "RU";
	}
}
