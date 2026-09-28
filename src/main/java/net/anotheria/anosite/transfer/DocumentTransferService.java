package net.anotheria.anosite.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.anotheria.anosite.shared.AnositeConfig;
import net.anotheria.anosite.util.staticutil.JerseyClientUtil;
import net.anotheria.asg.util.rest.ReplyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transfers cms documents to the instances of a target group.
 *
 * <p>This is the whole of the transfer logic. The generated actions only say which document was clicked and
 * what the editor picked in the dialog; everything else — whether this instance may transfer at all, which
 * documents belong to the transfer, in which order they go out and what came back — happens here, once,
 * instead of once per document type.
 *
 * <p>Documents are written with a {@code PUT} against the target's generated rest api, which upserts under the
 * id it is given. That keeps ids stable across systems, which is what makes links survive the trip.
 *
 * <p>In {@link TransferMode#DEEP} the engine walks the link graph from the selected document and sends
 * dependencies before the documents that point at them, so a target never sees a link to something that isn't
 * there yet. The walk visits every document once, which is what keeps cycles — a box whose sub box links back
 * to it — from turning into an endless transfer.
 *
 * @author Leon Rosenberg
 */
public class DocumentTransferService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentTransferService.class);

    /**
     * Serializes the snapshots' VOs. Thread safe once configured, which is why it is shared.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * How much of a target's error body makes it into the report. Enough for the reason, not enough to turn
     * the dialog into a stack trace viewer — the full body goes to the log.
     */
    private static final int MAX_REPORTED_BODY_LENGTH = 500;

    /**
     * Transfers a document, and in deep mode everything it links to, to every target of a group.
     *
     * @param root      the document the editor selected
     * @param mode      single document or the whole reachable subgraph
     * @param groupName name of the target group, as configured in {@code anositeconfig.json}
     * @return what happened, per target and per document
     * @throws DocumentTransferException if the transfer cannot be attempted at all
     */
    public TransferReport transfer(DocumentKey root, TransferMode mode, String groupName) throws DocumentTransferException {
        AnositeConfig config = AnositeConfig.getInstance();
        if (!config.isTransferEnabled())
            throw new DocumentTransferException("Transfer is not enabled on this instance, "
                    + "set transferEnabled in anositeconfig to allow it.");

        TransferTargetGroup group = config.getTransferTargetGroup(groupName);
        if (group == null)
            throw new DocumentTransferException("Unknown transfer target group: " + groupName);

        TransferPlan plan = plan(root, mode);
        List<DocumentSnapshot> snapshots = plan.documents();
        List<String> warnings = new ArrayList<>(plan.warnings());

        List<TargetTransferReport> targetReports = new ArrayList<>();
        for (TransferTarget target : group.getTargets()) {
            if (target == null || !target.isValid()) {
                warnings.add("Target without url in group " + group.getName() + " was skipped.");
                continue;
            }
            targetReports.add(transferTo(target, snapshots));
        }

        TransferReport report = new TransferReport(group.getName(), mode, plan.keys(), targetReports, warnings);
        LOG.info("Transferred {} in mode {} to group {}: {} document(s), success={}", root, mode, group.getName(),
                snapshots.size(), report.isSuccess());

        return report;
    }

    /**
     * The groups this instance can transfer to. Empty if transfer is switched off or nothing is configured.
     *
     * @return usable target groups
     */
    public List<TransferTargetGroup> getTargetGroups() {
        return AnositeConfig.getInstance().getUsableTransferTargetGroups();
    }

    /**
     * Works out what a transfer would send, without sending anything.
     *
     * <p>Split out from {@link #transfer(DocumentKey, TransferMode, String)} because it is the interesting
     * half: which documents belong to a deep transfer, in which order they have to go out, and what had to be
     * left out along the way. It needs neither configuration nor a reachable target.
     *
     * @param root document the editor selected
     * @param mode single document or deep
     * @return the documents to send, in send order, and what was skipped
     * @throws DocumentTransferException if the selected document's module cannot be transferred, or the
     *                                   document itself cannot be read
     */
    public TransferPlan plan(DocumentKey root, TransferMode mode) throws DocumentTransferException {
        List<String> warnings = new ArrayList<>();
        ModuleTransferSupport support = TransferSupportRegistry.get(root.moduleName());
        if (support == null)
            throw new DocumentTransferException("Module " + root.moduleName() + " cannot be transferred, "
                    + "no transfer support is registered for it.");

        DocumentSnapshot rootSnapshot;
        try {
            rootSnapshot = support.load(root.documentName(), root.id());
        } catch (Exception e) {
            throw new DocumentTransferException("Cannot load " + root + ": " + e.getMessage(), e);
        }

        List<DocumentSnapshot> collected = new ArrayList<>();
        Set<DocumentKey> visited = new HashSet<>();
        visited.add(root);

        if (mode == TransferMode.DEEP)
            for (DocumentKey reference : rootSnapshot.references())
                collectDeep(reference, visited, collected, warnings);

        collected.add(rootSnapshot);

        reportFiles(collected, warnings);
        return new TransferPlan(collected, warnings);
    }

    /**
     * Depth first walk of the link graph, appending each document after the documents it points at.
     *
     * @param key       document to visit
     * @param visited   documents already seen, cycle protection and duplicate protection in one
     * @param collected snapshots in send order
     * @param warnings  collects links that could not be followed
     */
    private void collectDeep(DocumentKey key, Set<DocumentKey> visited, List<DocumentSnapshot> collected, List<String> warnings) {
        if (!visited.add(key))
            return;

        ModuleTransferSupport support = TransferSupportRegistry.get(key.moduleName());
        if (support == null) {
            warnings.add("Skipped " + key + ": module " + key.moduleName() + " cannot be transferred.");
            return;
        }

        DocumentSnapshot snapshot;
        try {
            snapshot = support.load(key.documentName(), key.id());
        } catch (Exception e) {
            //a link pointing at a document that is gone is a content problem, not a reason to refuse the
            //transfer of everything else.
            warnings.add("Skipped " + key + ": " + e.getMessage());
            LOG.warn("Cannot load linked document {} for transfer", key, e);
            return;
        }

        for (DocumentKey reference : snapshot.references())
            collectDeep(reference, visited, collected, warnings);

        collected.add(snapshot);
    }

    /**
     * Notes the files the transferred documents point at. Binaries live in the file storage and are not part of
     * a document transfer, so the editor has to know which ones the target may still be missing.
     *
     * @param snapshots documents being transferred
     * @param warnings  collects one entry per document with files
     */
    private void reportFiles(List<DocumentSnapshot> snapshots, List<String> warnings) {
        for (DocumentSnapshot snapshot : snapshots)
            if (!snapshot.referencedFiles().isEmpty())
                warnings.add(snapshot.key() + " references file(s) " + String.join(", ", snapshot.referencedFiles())
                        + " which are not part of the transfer and have to be synced separately.");
    }

    /**
     * Sends every snapshot to one target, in order, and keeps going after a failure so one bad document does
     * not hide the state of the rest.
     *
     * @param target    where to send
     * @param snapshots what to send, in send order
     * @return per document outcome on this target
     */
    private TargetTransferReport transferTo(TransferTarget target, List<DocumentSnapshot> snapshots) {
        List<DocumentTransferResult> results = new ArrayList<>();
        for (DocumentSnapshot snapshot : snapshots)
            results.add(send(target, snapshot));

        return new TargetTransferReport(target.getName(), target.getNormalizedUrl(), results);
    }

    /**
     * Puts one document on one target.
     *
     * @param target   where to send
     * @param snapshot what to send
     * @return the outcome, never throws
     */
    private DocumentTransferResult send(TransferTarget target, DocumentSnapshot snapshot) {
        String url = documentUrl(target, snapshot);

        String body;
        try {
            body = MAPPER.writeValueAsString(snapshot.payload());
        } catch (Exception e) {
            return new DocumentTransferResult(snapshot.key(), false, "Cannot serialize document: " + e.getMessage());
        }

        Response response = null;
        try {
            response = JerseyClientUtil.getClientInstance()
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(body, MediaType.APPLICATION_JSON));

            String payload = response.readEntity(String.class);
            if (response.getStatus() < 200 || response.getStatus() >= 300) {
                LOG.warn("Transfer of {} to {} was rejected with HTTP {}: {}",
                        snapshot.key(), url, response.getStatus(), payload);
                return new DocumentTransferResult(snapshot.key(), false,
                        "HTTP " + response.getStatus() + " from " + url + describeBody(payload));
            }

            ReplyObject reply;
            try {
                reply = MAPPER.readValue(payload, ReplyObject.class);
            } catch (Exception e) {
                return new DocumentTransferResult(snapshot.key(), false,
                        "Could not read the answer of " + url + describeBody(payload));
            }

            if (!reply.isSuccess())
                return new DocumentTransferResult(snapshot.key(), false, reply.getMessage());

            return new DocumentTransferResult(snapshot.key(), true, null);
        } catch (Exception e) {
            LOG.warn("Transfer of {} to {} failed", snapshot.key(), url, e);
            return new DocumentTransferResult(snapshot.key(), false, e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * The target's own words about a failure, trimmed to something a dialog can show.
     *
     * <p>A bare "HTTP 400" tells the editor that it broke and nothing about why, while the body it came with
     * usually names the field that did it. Worth the few hundred characters.
     *
     * @param payload response body, possibly empty
     * @return the body prefixed with a separator, or an empty string if there was nothing to say
     */
    static String describeBody(String payload) {
        if (payload == null || payload.isBlank())
            return "";

        String trimmed = payload.trim();
        if (trimmed.length() > MAX_REPORTED_BODY_LENGTH)
            trimmed = trimmed.substring(0, MAX_REPORTED_BODY_LENGTH) + "...";

        return ": " + trimmed;
    }

    /**
     * Builds the url one document is written to on a target.
     *
     * @param target   target instance
     * @param snapshot document being sent
     * @return absolute url of the document on the target
     */
    private String documentUrl(TransferTarget target, DocumentSnapshot snapshot) {
        return target.getNormalizedUrl() + "/" + snapshot.restPath() + "/"
                + URLEncoder.encode(snapshot.key().id(), StandardCharsets.UTF_8);
    }
}
