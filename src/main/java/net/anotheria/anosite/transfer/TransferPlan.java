package net.anotheria.anosite.transfer;

import java.util.List;

/**
 * What a transfer would send, worked out before anything goes over the wire.
 *
 * @param documents snapshots in send order: a document always comes after the documents it links to, so a
 *                  target never sees a link to something that isn't there yet
 * @param warnings  links that could not be followed, modules that cannot be transferred and files that stay
 *                  behind
 */
public record TransferPlan(List<DocumentSnapshot> documents, List<String> warnings) {

    /**
     * The documents of this plan, identity only.
     *
     * @return keys in send order
     */
    public List<DocumentKey> keys() {
        return documents.stream().map(DocumentSnapshot::key).toList();
    }
}
