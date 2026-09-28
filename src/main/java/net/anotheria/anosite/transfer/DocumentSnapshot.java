package net.anotheria.anosite.transfer;

import java.util.List;

/**
 * One document as the transfer engine sees it: what to send, where to send it and what it points at.
 *
 * <p>Snapshots are produced by the generated {@code <Module>TransferSupport} classes, which are the only place
 * that knows a document's fields and links. Everything downstream — traversal, serialization, the http calls —
 * works on snapshots alone and never touches a document again.
 *
 * @param key           identity of the document
 * @param restPath      path of the document's rest collection relative to the target's api base, without
 *                      leading or trailing slash, e.g. {@code asresourcedata/localizationbundle}
 * @param payload       the document's rest VO, serialized to json by the engine
 * @param references    documents this one links to, single links and link lists alike, in declaration order
 * @param referencedFiles names of files this document points into the file storage with, e.g. the image of an
 *                      {@code Image} document. Binaries are not transferred; the engine reports them so the
 *                      editor knows what still has to be synced by hand
 */
public record DocumentSnapshot(DocumentKey key, String restPath, Object payload, List<DocumentKey> references,
                               List<String> referencedFiles) {
}
