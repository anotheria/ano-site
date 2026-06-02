package net.anotheria.anosite.guard;

import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.asg.data.DataObject;

/**
 * Abstract language guard.
 * 
 * @author Alexandr Bolbat
 */
public abstract class LanguageAbstractGuard implements ConditionalGuard {

	/**
	 * Target language.
	 * 
	 * @return {@link String} language code (configured code from ano-site definition)
	 */
	protected abstract String getTargetLanguage();

	/**
	 * By overriding this method we can change conditional logic.
	 * 
	 * @return <code>true</code> if target language should be equal to current logic or <code>false</code> if shouldn't
	 */
	protected boolean shouldMatch() {
		return true;
	}

	@Override
	public final boolean isConditionFullfilled(final DataObject object, final HttpServletRequest req) {
		return ContextManager.getCallContext().getCurrentLanguage().equals(getTargetLanguage()) == shouldMatch();
	}

}
