package net.anotheria.anosite.transfer;

import java.util.List;

/**
 * What happened on one target of the group.
 *
 * <p>Targets are independent: one unreachable node does not stop the transfer to the others, it just shows up
 * here as a failed target.
 *
 * @param targetName name of the target as configured
 * @param url        base url the documents were sent to
 * @param results    one entry per document, in the order they were sent
 */
public record TargetTransferReport(String targetName, String url, List<DocumentTransferResult> results) {

    /**
     * Whether every document made it.
     *
     * @return true if nothing failed on this target
     */
    public boolean isSuccess() {
        return failed() == 0;
    }

    /**
     * Number of documents the target accepted.
     *
     * @return count of successful documents
     */
    public int succeeded() {
        return (int) results.stream().filter(DocumentTransferResult::success).count();
    }

    /**
     * Number of documents the target rejected or never saw.
     *
     * @return count of failed documents
     */
    public int failed() {
        return results.size() - succeeded();
    }
}
