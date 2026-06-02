package net.anotheria.anosite.guard;

/**
 * Current language should be 'DE'.
 * 
 * @author Alexandr Bolbat
 */
public class LanguageIsDEGuard extends LanguageAbstractGuard {

	@Override
	protected String getTargetLanguage() {
		return "DE";
	}

}
