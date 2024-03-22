package net.anotheria.anosite.handler.validation;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Clean form bean. Used in handlers where we don't need form bean and submit.
 * 
 * @author Alexandr Bolbat
 */
public final class EmptyFormBean extends AbstractFormBean {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 3888810209768869496L;

	@Override
	public final Object getFieldValue(String fieldName) {
		return EMPTY_STRING;
	}

	@Override
	protected final void prepare(HttpServletRequest req) {
	}


}
