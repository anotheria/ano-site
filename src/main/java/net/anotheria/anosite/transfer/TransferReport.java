package net.anotheria.anosite.transfer;

import java.util.List;

/**
 * The outcome of one transfer, for the editor who triggered it.
 *
 * @param groupName  name of the target group the documents went to
 * @param mode       mode the transfer ran in
 * @param documents  the documents that were collected and sent, dependencies before the documents that link
 *                   to them
 * @param targets    one report per target in the group
 * @param warnings   things that did not stop the transfer but the editor should know about: links that could
 *                   not be followed, modules that cannot be transferred, files that were left behind
 */
public record TransferReport(String groupName, TransferMode mode, List<DocumentKey> documents,
                             List<TargetTransferReport> targets, List<String> warnings) {

    /**
     * Whether every document reached every target. A transfer that reached no target at all is not a success,
     * however quietly it went.
     *
     * @return true if there was at least one target and nothing failed anywhere
     */
    public boolean isSuccess() {
        return !targets.isEmpty() && targets.stream().allMatch(TargetTransferReport::isSuccess);
    }
}
