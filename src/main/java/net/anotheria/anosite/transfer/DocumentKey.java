package net.anotheria.anosite.transfer;

/**
 * Identifies one cms document: the module it lives in, its type and its id.
 *
 * <p>The transfer engine uses it as the node identity of the link graph, so two keys are equal exactly when
 * they point at the same document. That is what keeps a deep transfer from walking in circles — boxes linking
 * to sub boxes linking back to their parent are a normal thing in a cms.
 *
 * @param moduleName   name of the module as written in the datadef, e.g. {@code ASResourceData}
 * @param documentName name of the document as written in the datadef, e.g. {@code LocalizationBundle}
 * @param id           id of the document
 */
public record DocumentKey(String moduleName, String documentName, String id) {

    /**
     * Full name of the document type, module and document separated by a dot, the notation link targets use in
     * the datadef.
     *
     * @return {@code Module.Document}
     */
    public String documentType() {
        return moduleName + "." + documentName;
    }

    @Override
    public String toString() {
        return documentType() + "#" + id;
    }
}
