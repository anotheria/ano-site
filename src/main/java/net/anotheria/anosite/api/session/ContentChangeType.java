package net.anotheria.anosite.api.session;

/**
 * Describes a change type of a cms change.
 * @author lrosenberg
 */
public enum ContentChangeType {
	/**
	 * A document has been created.
	 */
	CREATE,
	/**
	 * A document has been deleted.
	 */
	DELETE,
	/**
	 * A document has been updated.
	 */
	UPDATE,

    /**
     * A document has been imported.
     */
    IMPORT
}
