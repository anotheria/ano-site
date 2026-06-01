package net.anotheria.anosite.guard;

/**
 * Current language should be 'EN'.
 * 
 * @author Alexandr Bolbat
 */
public class LanguageIsENGuard extends LanguageAbstractGuard {

	@Override
	protected String getTargetLanguage() {
		return "EN";
	}

}
