package net.anotheria.anosite.api.session;

/**
 * This is yet experimental feature for notification of caching api's that a content piece they were previously caching has changed.
 * @author another
 *
 */
public class ContentChangeEvent {
	/**
	 * Name of the changed document.
	 */
	private String documentName;
	/**
	 * Document change type.
	 */
	private ContentChangeType changeType;
	/**
	 * Ids of the changed document.
	 */
	private String documentId;

	/**
	 * Default Constructor.
	 */
	public ContentChangeEvent(){
		
	}

	/**
	 * Constructor.
	 * 
	 * @param aType actually operation
	 * @param aDocumentName name of document
	 * @param aDocumentId doc id
	 */
	public ContentChangeEvent(ContentChangeType aType, String aDocumentName, String aDocumentId){
		changeType = aType;
		documentName = aDocumentName;
		documentId = aDocumentId;
	}
	
	@Override public String toString(){
		return getChangeType()+" "+getDocumentName()+": "+getDocumentId();
	}
	
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public ContentChangeType getChangeType() {
		return changeType;
	}
	public void setChangeType(ContentChangeType changeType) {
		this.changeType = changeType;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
}
