package net.anotheria.anosite.guard;

/**
 * This conditional guard is only fulfilled if there is NO currentUserId attribute in the session indicating that there is NO logged in user. 
 * @author lrosenberg
 *
 */
public class CMSLoggedOutGuard extends CMSLogedInGuard{

	@Override
	protected boolean getDesiredResult() {
		return false;
	}
	
}
