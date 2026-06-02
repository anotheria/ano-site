package net.anotheria.anosite.guard;

import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.asg.data.DataObject;

/**
 * A guard which only returns true if the desired attribute is in session.
 * @author lrosenberg
 *
 */
public abstract class SessionFlagPresentGuard implements ConditionalGuard{

	@Override
	public boolean isConditionFullfilled(DataObject object, HttpServletRequest req) {
		return (req.getSession().getAttribute(getFlagName())!=null) == getDesiredResult();
	}
	
	/**
	 * If true the guard is fullfilled whenever the attribute with name = flagName is in session. If false the guard is fullfilled if the attribute with the name = flagName is NOT in session.
	 */
	protected boolean getDesiredResult(){
		return true;
	}
	/**
	 * Returns the name of the flag to look after.
	 * @return flag name.
	 */
	protected abstract String getFlagName();
	
}
