package net.anotheria.anosite.api.session;

/**
 * Should be implemented by all Objects which are a representation of a content object of some kind.
 * The api will update all content aware attributes of a content change if the ContentChangeListener is connected.
 * @author lrosenberg
 *
 */
public interface ContentAwareAttribute {
	
	/**
	 * Called first, if this returns true, the object will be removed from any cache its in.
	 * @return boolean param
	 */
	boolean deleteOnChange();
	
	/**
	 * If delete on change returns false this method is called.
	 * @param documentName name
	 * @param documentId id
	 */
	void notifyContentChange(String documentName, String documentId);
}
