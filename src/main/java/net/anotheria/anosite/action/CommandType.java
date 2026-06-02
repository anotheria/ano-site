package net.anotheria.anosite.action;
/**
 * Types of action commands.
 * @author lrosenberg
 */
public enum CommandType {
	/**
	 * Use if the action handles its execution itself, for example by sending a redirect directly (which is not recommended but allowed. 
	 */
	None,
	/**
	 * Forward to another page/action.
	 */
	Forward,
	/**
	 * Redirect to another page/url.
	 */
	Redirect,
	
}
