package net.anotheria.anosite.guard;

import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.util.BasicComparable;

/**
 * Basic class for guards based on anodoc context language. Please note that anodoc context language is not the same as locale language and is more a representation
 * of a complete locale or a site variant. For example we usually represent german by DE and swiss german by CH_DE, which is opposite to what you would do with locales.
 * @author lrosenberg
 *
 */
public abstract class ContextLanguageIsGuard implements ConditionalGuard{

	@Override
	public boolean isConditionFullfilled(DataObject object,
			HttpServletRequest req) throws ASGRuntimeException {
		
		String contextLanguage = ContextManager.getCallContext().getCurrentLanguage();
		String desiredLanguage = getDesiredContextLanguage();
		
		return BasicComparable.compareString(contextLanguage, desiredLanguage)==0;
	}

	/**
	 * Returns the context language for this guard implementation to let pass. Remember the return value of this function must MATCH EXACTLY what you've written in the context.xml.
	 * @return context language.
	 */
	protected abstract String getDesiredContextLanguage();
	
}
