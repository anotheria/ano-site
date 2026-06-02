package net.anotheria.anosite.guard;

/**
 * Current language shouldn't be 'EN'.
 * 
 * @author Alexandr Bolbat
 */
public class LanguageIsNotENGuard extends LanguageIsENGuard {

	@Override
	protected boolean shouldMatch() {
		return false;
	}

}
