package net.anotheria.anosite.guard;

/**
 * This conditional guard is only fulfilled if there is a currentUserId attribute in the session indicating that there is a logged in user. 
 * @author lrosenberg
 *
 */
public class CMSLogedInGuard extends SessionFlagPresentGuard{
	@Override protected String getFlagName(){
		return "currentUserId";
	}
}
