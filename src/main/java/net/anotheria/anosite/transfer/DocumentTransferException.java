package net.anotheria.anosite.transfer;

/**
 * Thrown when a transfer cannot even be attempted: transfer is switched off here, the target group is unknown,
 * the document's module cannot be transferred or the document itself cannot be loaded.
 *
 * <p>Failures of individual documents on individual targets are not exceptions — they are part of the
 * {@link TransferReport}, because a transfer to four nodes that reaches three of them is a result, not a
 * crash.
 */
public class DocumentTransferException extends Exception {

    private static final long serialVersionUID = 1L;

    public DocumentTransferException(String message) {
        super(message);
    }

    public DocumentTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
