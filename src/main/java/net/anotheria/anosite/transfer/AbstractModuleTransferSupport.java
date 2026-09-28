package net.anotheria.anosite.transfer;

import java.util.List;

/**
 * Base class of the generated module transfer supports, holding the bits of bookkeeping that would otherwise
 * be generated into every single document method.
 *
 * <p>The helpers all skip empty values on purpose: an unset link is an empty string in the cms, not null, and
 * an empty link is not a reference to anything.
 */
public abstract class AbstractModuleTransferSupport implements ModuleTransferSupport {

    /**
     * Adds the target of a single link, if the link is set.
     *
     * @param references   collected references of the document being described
     * @param moduleName   module of the link target
     * @param documentName document type of the link target
     * @param id           value of the link, possibly empty
     */
    protected void addReference(List<DocumentKey> references, String moduleName, String documentName, String id) {
        if (id != null && !id.trim().isEmpty())
            references.add(new DocumentKey(moduleName, documentName, id.trim()));
    }

    /**
     * Adds the targets of a link list, in list order.
     *
     * @param references   collected references of the document being described
     * @param moduleName   module of the link targets
     * @param documentName document type of the link targets
     * @param ids          values of the link list, possibly null or empty
     */
    protected void addReferences(List<DocumentKey> references, String moduleName, String documentName, List<String> ids) {
        if (ids == null)
            return;

        for (String id : ids)
            addReference(references, moduleName, documentName, id);
    }

    /**
     * Adds a file the document points at in the file storage, if there is one.
     *
     * @param files    collected file names of the document being described
     * @param fileName value of an image or file property, possibly empty
     */
    protected void addFile(List<String> files, String fileName) {
        if (fileName != null && !fileName.trim().isEmpty())
            files.add(fileName.trim());
    }

    /**
     * Adds the files of an image list property.
     *
     * @param files     collected file names of the document being described
     * @param fileNames values of the list, possibly null or empty
     */
    protected void addFiles(List<String> files, List<String> fileNames) {
        if (fileNames == null)
            return;

        for (String fileName : fileNames)
            addFile(files, fileName);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{module='" + getModuleName() + "'}";
    }
}
