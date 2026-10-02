package net.anotheria.anosite.cms.listener;

import net.anotheria.anosite.transfer.AutoTransferService;
import net.anotheria.anosite.transfer.DocumentKey;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.IServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hands every change in a module to the auto transfer.
 *
 * <p>Configure it per module in the datadef, next to the other listeners:
 * <pre>
 * &lt;listener class="net.anotheria.anosite.cms.listener.AutoTransferListener" /&gt;
 * </pre>
 * Whether it then publishes anything is a question of {@code anositeconfig.json} alone — a listener on an
 * instance without an {@code autoTransfer} group does nothing at all. That is why it can be configured for
 * every module of a project and left there: the same war runs as the cms that publishes and as the system that
 * is published to.
 *
 * <p>There used to be one subclass of this listener per module, because the old auto transfer copied a
 * module's {@code .dat} file and had to be told which file. A document knows its own module and type
 * ({@link DataObject#getDefinedParentName()}, {@link DataObject#getDefinedName()}), and the rest api transfer
 * needs nothing else, so one listener covers every module and project modules alike.
 *
 * @author Leon Rosenberg
 */
public class AutoTransferListener implements IServiceListener {

    private static final Logger LOG = LoggerFactory.getLogger(AutoTransferListener.class);

    /** {@inheritDoc} */
    @Override
    public void documentCreated(DataObject document) {
        publish(document);
    }

    /**
     * {@inheritDoc}
     *
     * <p>A save that changed nothing is not published. Two reasons, and the second one matters more: an editor
     * opening a dialog and pressing save does not need to go out to four instances, and an instance that is
     * both a transfer target and configured to auto transfer — which it should not be, but configuration
     * happens — would otherwise bounce a document back and forth forever. An incoming transfer writes exactly
     * what the sender has, so the footprint is unchanged, and the bouncing stops after one hop.
     */
    @Override
    public void documentUpdated(DataObject oldVersion, DataObject newVersion) {
        if (unchanged(oldVersion, newVersion)) {
            LOG.debug("Not auto transferring {}, the save changed nothing", key(newVersion));
            return;
        }

        publish(newVersion);
    }

    /** {@inheritDoc} */
    @Override
    public void documentDeleted(DataObject document) {
        if (document == null || !AutoTransferService.getInstance().isEnabled())
            return;

        AutoTransferService.getInstance().documentDeleted(key(document));
    }

    /**
     * {@inheritDoc}
     *
     * <p>An import is a document arriving from somewhere else — the xml import in the cms, or another instance
     * transferring into this one. The first is content an editor wants published like any other; the second
     * cannot happen on an instance that publishes, because being a transfer target and transferring are not
     * meant to be combined.
     */
    @Override
    public void documentImported(DataObject document) {
        publish(document);
    }

    /** {@inheritDoc} */
    @Override
    public void persistenceChanged() {
        //a reloaded module is not a change an editor made, and republishing all of it would be wrong.
    }

    /**
     * Queues a document, unless nothing is configured to publish into.
     *
     * @param document the document that changed
     */
    private void publish(DataObject document) {
        if (document == null || !AutoTransferService.getInstance().isEnabled())
            return;

        AutoTransferService.getInstance().documentChanged(key(document));
    }

    /**
     * Whether a save left the document's content as it was.
     *
     * @param oldVersion the document before the save, may be null
     * @param newVersion the document after the save
     * @return true if both versions have the same footprint
     */
    static boolean unchanged(DataObject oldVersion, DataObject newVersion) {
        if (oldVersion == null || newVersion == null)
            return false;

        String before = oldVersion.getObjectInfo().getFootprint();
        return before != null && before.equals(newVersion.getObjectInfo().getFootprint());
    }

    /**
     * Identity of a document, the way the transfer engine names one.
     *
     * @param document the document
     * @return its key
     */
    static DocumentKey key(DataObject document) {
        return new DocumentKey(document.getDefinedParentName(), document.getDefinedName(), document.getId());
    }
}
