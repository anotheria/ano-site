package net.anotheria.anosite.shared;

/**
 * A code which signalizes how to perform further rendering.
 * @author lrosenberg
 */
public enum InternalResponseCode {
	/**
	 * Continue execution.
	 */
	CONTINUE,
	/**
	 * Abort the exception, an exception should be thrown.
	 */
	ABORT,
	/**
	 * Send the redirect to the client instead of page rendering. The Redirect will be executed at the END of the 
	 * processing of all Handler. 
	 */
	CONTINUE_AND_REDIRECT,
	/**
	 * Cancel further execution and redirect immediately.
	 */
	CANCEL_AND_REDIRECT,
	/**
	 * A part of the exception encountered an error, but the application can still resume processing, only a part is broken.
	 */
	ERROR_AND_CONTINUE,
	/**
	 * Stop all further processing, since the request has been handled.
	 */
	STOP
	
	
}
