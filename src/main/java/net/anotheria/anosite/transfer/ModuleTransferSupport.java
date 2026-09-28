package net.anotheria.anosite.transfer;

import java.util.List;

/**
 * Everything the transfer engine needs to know about one module's documents.
 *
 * <p>Implemented by the generated {@code <Module>TransferSupport} classes — a document's fields, its rest path
 * and its links are generation time knowledge, and this is the interface through which the engine gets at them
 * without knowing a single document type.
 *
 * <p>Implementations are looked up by module name in the {@link TransferSupportRegistry}. They are stateless
 * and used from request threads, so they have to be thread safe.
 */
public interface ModuleTransferSupport {

    /**
     * Name of the module this support covers, as written in the datadef.
     *
     * @return module name, e.g. {@code ASResourceData}
     */
    String getModuleName();

    /**
     * Names of the documents in this module.
     *
     * @return document names, e.g. {@code [TextResource, LocalizationBundle, ...]}
     */
    List<String> getDocumentNames();

    /**
     * Loads a document and describes it for transfer.
     *
     * @param documentName name of the document type, one of {@link #getDocumentNames()}
     * @param id           id of the document
     * @return the snapshot to transfer
     * @throws IllegalArgumentException if this module has no such document type
     * @throws Exception                if the document cannot be loaded
     */
    DocumentSnapshot load(String documentName, String id) throws Exception;
}
