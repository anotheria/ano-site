package net.anotheria.anosite.api.generic.login.processors;

import net.anotheria.anosite.api.common.APICallContext;
import net.anotheria.anosite.api.generic.login.LogoutPostProcessor;

/**
 * Cleans a session after logout to remove all objects that belong to a logged in user only. If the object is put under 
 * the policy SURVIVE_LOGOUT it will survive the logout. 
 * @author lrosenberg
 */
public class SessionCleanupOnLogoutProcessor implements LogoutPostProcessor{

	@Override public void postProcessLogout(String userId) {
		APICallContext.getCallContext().getCurrentSession().cleanupOnLogout();
	}
	
}
