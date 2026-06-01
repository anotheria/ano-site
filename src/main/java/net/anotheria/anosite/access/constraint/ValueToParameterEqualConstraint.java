package net.anotheria.anosite.access.constraint;

import net.anotheria.access.impl.AccessContext;

/**
 * This constraint use two parameters configured in CMS, first parameter is name of security context attribute, second parameter is value with what first value
 * should be checked.
 * 
 * @author Alexandr Bolbat
 */
public class ValueToParameterEqualConstraint extends ParametrizedConstraint {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -5701767318869717338L;

	@Override
	public boolean isMet() {
		String valueToCheck = AccessContext.getContext().getObject().getAttributeValue(getParameter1());
		String etalonValue = getParameter2();

		if (valueToCheck == null && etalonValue == null)
			return true;

		if (etalonValue != null && valueToCheck == null)
			return false;

		if (etalonValue == null && valueToCheck != null)
			return false;

		return etalonValue.equals(valueToCheck);
	}

}
