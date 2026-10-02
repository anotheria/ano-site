package net.anotheria.anosite.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Publishes every change an editor makes into the target groups configured with {@code autoTransfer}.
 *
 * <p>This replaces the old auto transfer, which copied a module's {@code .dat} file and the image files next
 * to it into another installation's data directory. That only ever worked because the cms and the systems it
 * fed shared a file system; a cms on its own host has nothing to copy into. The transfer engine already knows
 * how to write a document into another instance over its rest api, so the auto transfer is now that same
 * transfer without an editor clicking it, and the old configuration file is gone — a group says
 * {@code autoTransfer: true} in {@code anositeconfig.json} and that is the whole of it.
 *
 * <p>Transfers run on a single background thread. Two reasons: an editor pressing save must not wait for
 * however many instances to answer, and one thread keeps the writes in the order they were made, so a document
 * saved twice in a row does not arrive in the wrong order. A queue that falls behind delays publishing, it
 * does not lose it; a transfer that fails is logged and not retried, because the next save of that document
 * sends it again and a retry loop against an instance that is down helps nobody.
 *
 * <p>Documents go out one at a time, in {@link TransferMode#SINGLE}: every document is published as it is
 * saved, so the link graph catches up by itself, while a deep transfer on every save would re-publish half the
 * cms each time an editor fixes a typo.
 *
 * @author Leon Rosenberg
 */
public final class AutoTransferService {

    private static final Logger LOG = LoggerFactory.getLogger(AutoTransferService.class);

    /**
     * The one instance. The listeners the generated services create are one per module, and they all queue
     * into the same thread.
     */
    private static final AutoTransferService INSTANCE = new AutoTransferService();

    /**
     * The transfer thread. Single threaded on purpose, see the class comment.
     */
    private final ExecutorService executor;

    /**
     * Does the actual transferring. Stateless, so one is enough.
     */
    private final DocumentTransferService transferService = new DocumentTransferService();

    private AutoTransferService() {
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable, "auto-transfer");
            //an unfinished queue must not keep a shutting down cms alive.
            thread.setDaemon(true);
            return thread;
        };
        executor = Executors.newSingleThreadExecutor(factory);
    }

    /**
     * <p>Getter for the one instance.</p>
     *
     * @return the auto transfer service
     */
    public static AutoTransferService getInstance() {
        return INSTANCE;
    }

    /**
     * Publishes a document that was created or changed.
     *
     * @param key the document
     */
    public void documentChanged(DocumentKey key) {
        submit(key, false);
    }

    /**
     * Deletes a document that was deleted here on every auto transfer target.
     *
     * @param key the document
     */
    public void documentDeleted(DocumentKey key) {
        submit(key, true);
    }

    /**
     * Whether anything would be published at all. Asked before a change is queued so an instance that does not
     * auto transfer — which is every instance until one is configured for it — does no work per saved document.
     *
     * @return true if at least one usable group has auto transfer enabled
     */
    public boolean isEnabled() {
        return !transferService.getAutoTransferTargetGroups().isEmpty();
    }

    /**
     * Queues one document.
     *
     * @param key     the document
     * @param deleted true to delete it on the targets, false to write it there
     */
    private void submit(DocumentKey key, boolean deleted) {
        if (key == null || key.id() == null || key.id().trim().isEmpty())
            return;

        List<TransferTargetGroup> groups = transferService.getAutoTransferTargetGroups();
        if (groups.isEmpty())
            return;

        //the groups are read here and not in the transfer thread: this is what the configuration said when
        //the document changed, and a reload in between should not retarget a change that was already made.
        try {
            executor.execute(() -> run(key, deleted, groups));
        } catch (Exception e) {
            //a rejected execution means the cms is shutting down. Losing the auto transfer of a document is
            //not a reason to fail the save it belongs to.
            LOG.warn("Could not queue the auto transfer of {}", key, e);
        }
    }

    /**
     * Transfers or deletes one document in every group, on the transfer thread.
     *
     * @param key     the document
     * @param deleted true to delete it on the targets, false to write it there
     * @param groups  the groups to publish into
     */
    private void run(DocumentKey key, boolean deleted, List<TransferTargetGroup> groups) {
        for (TransferTargetGroup group : groups) {
            try {
                TransferReport report = deleted
                        ? transferService.delete(key, group)
                        : transferService.transfer(key, TransferMode.SINGLE, group);

                if (report.isSuccess()) {
                    LOG.debug("Auto transferred {} to group {}", key, group.getName());
                    continue;
                }

                //this is the only place an auto transfer failure can be reported: nobody is looking at a
                //dialog, so the log is what an admin has to be able to read afterwards.
                LOG.warn("Auto transfer of {} to group {} did not reach every target: {}", key, group.getName(),
                        describe(report));
            } catch (DocumentTransferException e) {
                LOG.warn("Auto transfer of {} to group {} was refused: {}", key, group.getName(), e.getMessage());
            } catch (Exception e) {
                LOG.error("Auto transfer of {} to group {} failed", key, group.getName(), e);
            }
        }
    }

    /**
     * The failures of a report in one line.
     *
     * @param report a finished transfer
     * @return what went wrong, per target
     */
    private String describe(TransferReport report) {
        if (report.targets().isEmpty())
            return "group has no usable target";

        StringBuilder failures = new StringBuilder();
        for (TargetTransferReport target : report.targets()) {
            if (target.isSuccess())
                continue;

            for (DocumentTransferResult result : target.results())
                if (!result.success()) {
                    if (failures.length() > 0)
                        failures.append("; ");
                    failures.append(target.targetName()).append(": ").append(result.message());
                }
        }

        return failures.toString();
    }
}
