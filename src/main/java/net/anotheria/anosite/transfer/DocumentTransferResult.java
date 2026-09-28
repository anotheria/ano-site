package net.anotheria.anosite.transfer;

/**
 * What happened to one document on one target.
 *
 * @param key     the document
 * @param success true if the target accepted it
 * @param message the target's or the client's complaint, null on success
 */
public record DocumentTransferResult(DocumentKey key, boolean success, String message) {
}
