package net.anotheria.anosite.action;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.session.APISessionImpl;

public abstract class AbstractAction implements Action{
	
	/**
	 * no comment.
	 * @param name
	 * @param attribute
	 */
	protected void sendAttributeToPage(String name, Object attribute){
		((APISessionImpl)APICallContext.getCallContext().getCurrentSession()).addAttributeToActionScope(name, attribute);
	}
}
