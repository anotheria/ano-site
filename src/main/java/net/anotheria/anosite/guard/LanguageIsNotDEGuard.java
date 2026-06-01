package net.anotheria.anosite.guard;

/**
 * Current language shouldn't be 'DE'.
 * 
 * @author Alexandr Bolbat
 */
public class LanguageIsNotDEGuard extends LanguageIsDEGuard {

	@Override
	protected boolean shouldMatch() {
		return false;
	}

}
