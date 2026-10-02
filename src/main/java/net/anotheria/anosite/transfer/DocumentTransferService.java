package net.anotheria.anosite.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.anotheria.anosite.shared.AnositeConfig;
import net.anotheria.anosite.util.staticutil.JerseyClientUtil;
import net.anotheria.asg.util.filestorage.FileStorage;
import net.anotheria.asg.util.filestorage.TemporaryFileHolder;
import net.anotheria.asg.util.rest.ReplyObject;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
 * <p>The files a document points at travel with it, through the target's generated upload resource. A document
 * carries the name of its image, not the image, and a target that has the one without the other renders a
 * broken page — which is why copying files by hand stopped being an option when the cms moved to its own host.
 *
 * <p>Both halves of the cms transfer run through here: the editor clicking transfer in a dialog, and the
 * {@link AutoTransferService} publishing every change into the groups configured with {@code autoTransfer}.
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
     * Path of the generated resource that takes files into a target's file storage, relative to its api base.
     * Mirrors the {@code @Path} of the generated {@code UploadImageResource}.
     */
    private static final String FILE_UPLOAD_PATH = "asgimage/upload";

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
        requireTransferEnabled();

        TransferTargetGroup group = AnositeConfig.getInstance().getTransferTargetGroup(groupName);
        if (group == null)
            throw new DocumentTransferException("Unknown transfer target group: " + groupName);

        return transfer(root, mode, group);
    }

    /**
     * Transfers a document, and in deep mode everything it links to, to every target of a group.
     *
     * <p>Takes the group itself rather than its name, for the auto transfer: it has the groups from the
     * configuration in its hand already and would only look up what it just read.
     *
     * @param root  the document to transfer
     * @param mode  single document or the whole reachable subgraph
     * @param group the group to transfer into
     * @return what happened, per target and per document
     * @throws DocumentTransferException if the transfer cannot be attempted at all
     */
    public TransferReport transfer(DocumentKey root, TransferMode mode, TransferTargetGroup group) throws DocumentTransferException {
        requireTransferEnabled();

        TransferPlan plan = plan(root, mode);
        List<DocumentSnapshot> snapshots = plan.documents();
        List<String> warnings = new ArrayList<>(plan.warnings());

        List<TargetTransferReport> targetReports = new ArrayList<>();
        for (TransferTarget target : group.getTargets()) {
            if (target == null || !target.isValid()) {
                warnings.add("Target without url in group " + group.getName() + " was skipped.");
                continue;
            }
            targetReports.add(transferTo(target, snapshots, warnings));
        }

        TransferReport report = new TransferReport(group.getName(), mode, plan.keys(), targetReports, warnings);
        rememberTransfer(report);
        LOG.info("Transferred {} in mode {} to group {}: {} document(s), success={}", root, mode, group.getName(),
                snapshots.size(), report.isSuccess());

        return report;
    }

    /**
     * Deletes a document on every target of a group.
     *
     * <p>The counterpart of a transfer, and the reason the auto transfer can keep a target in sync at all: a
     * document an editor deleted here has to disappear there too, or the target keeps serving content that
     * does not exist anymore.
     *
     * <p>Unlike a transfer this does not read the document — it is gone by the time anybody can react to its
     * deletion — so it never walks links and has no mode. A target that does not have the document reports
     * success: the point is that it is not there afterwards.
     *
     * @param key   the document to delete
     * @param group the group to delete it in
     * @return what happened, per target
     * @throws DocumentTransferException if the deletion cannot be attempted at all
     */
    public TransferReport delete(DocumentKey key, TransferTargetGroup group) throws DocumentTransferException {
        requireTransferEnabled();

        ModuleTransferSupport support = TransferSupportRegistry.get(key.moduleName());
        if (support == null)
            throw new DocumentTransferException("Module " + key.moduleName() + " cannot be transferred, "
                    + "no transfer support is registered for it.");

        String restPath = support.getRestPath(key.documentName());
        List<String> warnings = new ArrayList<>();
        List<TargetTransferReport> targetReports = new ArrayList<>();
        for (TransferTarget target : group.getTargets()) {
            if (target == null || !target.isValid()) {
                warnings.add("Target without url in group " + group.getName() + " was skipped.");
                continue;
            }
            targetReports.add(new TargetTransferReport(target.getName(), target.getNormalizedUrl(),
                    List.of(deleteOn(target, key, restPath))));
        }

        TransferReport report = new TransferReport(group.getName(), TransferMode.SINGLE, List.of(key),
                targetReports, warnings);
        LOG.info("Deleted {} on group {}: success={}", key, group.getName(), report.isSuccess());

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
     * The groups that publish every change by themselves. Empty if transfer is switched off here.
     *
     * @return usable target groups with {@code autoTransfer} on
     */
    public List<TransferTargetGroup> getAutoTransferTargetGroups() {
        return AnositeConfig.getInstance().getAutoTransferTargetGroups();
    }

    /**
     * Refuses everything on an instance that was not set up to publish.
     *
     * @throws DocumentTransferException if transfer is off here
     */
    private void requireTransferEnabled() throws DocumentTransferException {
        if (!AnositeConfig.getInstance().isTransferEnabled())
            throw new DocumentTransferException("Transfer is not enabled on this instance, "
                    + "set transferEnabled in anositeconfig to allow it.");
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
     * Sends every snapshot to one target, in order, and keeps going after a failure so one bad document does
     * not hide the state of the rest.
     *
     * <p>The files a document points at go first: a target that has the document but not its image renders a
     * broken page, and the document is what makes the image reachable. Each file is sent once per target even
     * when several documents point at it.
     *
     * @param target    where to send
     * @param snapshots what to send, in send order
     * @param warnings  collects the files that could not be sent
     * @return per document outcome on this target
     */
    private TargetTransferReport transferTo(TransferTarget target, List<DocumentSnapshot> snapshots, List<String> warnings) {
        List<DocumentTransferResult> results = new ArrayList<>();
        Set<String> sentFiles = new HashSet<>();
        for (DocumentSnapshot snapshot : snapshots) {
            for (String fileName : snapshot.referencedFiles())
                if (sentFiles.add(fileName)) {
                    String failure = sendFile(target, fileName);
                    if (failure != null)
                        warnings.add(failure);
                }

            results.add(send(target, snapshot));
        }

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

            return readReply(snapshot.key(), url, payload);
        } catch (Exception e) {
            LOG.warn("Transfer of {} to {} failed", snapshot.key(), url, e);
            return new DocumentTransferResult(snapshot.key(), false, e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * Reads what a target answered to a write.
     *
     * <p>A target can say no twice: with an http status, and with a {@link ReplyObject} that carries
     * {@code success: false} inside a perfectly fine 200. Both are failures and both belong in the report.
     *
     * @param key     the document the answer is about
     * @param url     url that was written to, for the message
     * @param payload the response body
     * @return the outcome the body describes
     */
    private DocumentTransferResult readReply(DocumentKey key, String url, String payload) {
        ReplyObject reply;
        try {
            reply = MAPPER.readValue(payload, ReplyObject.class);
        } catch (Exception e) {
            return new DocumentTransferResult(key, false,
                    "Could not read the answer of " + url + describeBody(payload));
        }

        if (!reply.isSuccess())
            return new DocumentTransferResult(key, false, reply.getMessage());

        return new DocumentTransferResult(key, true, null);
    }

    /**
     * Puts one file of the cms file storage into the target's file storage.
     *
     * <p>Documents carry the <i>name</i> of a file, not its bytes, so transferring the document alone used to
     * leave the editor with a list of files to copy by hand — which stopped working the moment the cms moved
     * to its own host. The target's generated upload resource stores the file under the name it is sent with,
     * which is the name the document points at, so an overwrite is exactly what is wanted here.
     *
     * @param target   where to send
     * @param fileName name of the file in the cms file storage
     * @return null if the file arrived, a message for the report if it did not
     */
    private String sendFile(TransferTarget target, String fileName) {
        String url = target.getNormalizedUrl() + "/" + FILE_UPLOAD_PATH;

        TemporaryFileHolder file = FileStorage.loadFile(fileName);
        if (file == null || file.getData() == null) {
            LOG.warn("File {} is not in the file storage of this instance, not sending it to {}", fileName, url);
            return "File " + fileName + " is not in the file storage of this instance and was not transferred.";
        }

        String mimeType = file.getMimeType();
        Response response = null;
        try (FormDataMultiPart multipart = new FormDataMultiPart()) {
            multipart.bodyPart(new StreamDataBodyPart("file", new ByteArrayInputStream(file.getData()), fileName,
                    mimeType == null ? MediaType.APPLICATION_OCTET_STREAM_TYPE : MediaType.valueOf(mimeType)));

            response = JerseyClientUtil.getClientInstance()
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(multipart, multipart.getMediaType()));

            if (response.getStatus() < 200 || response.getStatus() >= 300) {
                String payload = response.readEntity(String.class);
                LOG.warn("Upload of file {} to {} was rejected with HTTP {}: {}", fileName, url,
                        response.getStatus(), payload);
                return "File " + fileName + " was rejected by " + url + ": HTTP " + response.getStatus()
                        + describeBody(payload);
            }

            return null;
        } catch (Exception e) {
            LOG.warn("Upload of file {} to {} failed", fileName, url, e);
            return "File " + fileName + " could not be sent to " + url + ": "
                    + e.getClass().getSimpleName() + ": " + e.getMessage();
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * Deletes one document on one target.
     *
     * @param target   where to delete
     * @param key      the document
     * @param restPath path of the document's rest collection on the target
     * @return the outcome, never throws
     */
    private DocumentTransferResult deleteOn(TransferTarget target, DocumentKey key, String restPath) {
        String url = target.getNormalizedUrl() + "/" + restPath + "/"
                + URLEncoder.encode(key.id(), StandardCharsets.UTF_8);

        Response response = null;
        try {
            response = JerseyClientUtil.getClientInstance()
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

            String payload = response.readEntity(String.class);
            //a target that does not have the document answers with a successful ReplyObject - the generated
            //resource deletes an id that isn't there without complaining, which is the state this asked for.
            //A 404 means the api is not where the target url says it is, and that is worth reporting.
            if (response.getStatus() < 200 || response.getStatus() >= 300) {
                LOG.warn("Deletion of {} on {} was rejected with HTTP {}: {}", key, url, response.getStatus(), payload);
                return new DocumentTransferResult(key, false,
                        "HTTP " + response.getStatus() + " from " + url + describeBody(payload));
            }

            return readReply(key, url, payload);
        } catch (Exception e) {
            LOG.warn("Deletion of {} on {} failed", key, url, e);
            return new DocumentTransferResult(key, false, e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * Writes the time of this transfer onto every document that reached every target.
     *
     * <p>That is what the {@code lastTransferTs} in the footer of the edit dialog reads, so an editor can see
     * whether what they are looking at has been published since they last changed it. A document that failed
     * on one of four nodes keeps its old timestamp: it is not on that system, and saying it is would be worse
     * than saying nothing.
     *
     * <p>Bookkeeping never fails a transfer — the documents are on the targets either way, and a report that
     * blows up afterwards would say they are not.
     *
     * @param report the finished transfer
     */
    private void rememberTransfer(TransferReport report) {
        if (report.targets().isEmpty())
            return;

        //grouped by module and type because writing the timestamp means writing the module: a deep transfer
        //of a few hundred documents is one module write per module this way, not one per document.
        Map<String, Map<String, List<String>>> byModule = new LinkedHashMap<>();
        for (DocumentKey key : report.documents())
            if (reachedEveryTarget(report, key))
                byModule.computeIfAbsent(key.moduleName(), module -> new LinkedHashMap<>())
                        .computeIfAbsent(key.documentName(), document -> new ArrayList<>())
                        .add(key.id());

        long now = System.currentTimeMillis();
        for (Map.Entry<String, Map<String, List<String>>> entry : byModule.entrySet()) {
            ModuleTransferSupport support = TransferSupportRegistry.get(entry.getKey());
            if (support == null)
                continue;

            try {
                support.markTransferred(entry.getValue(), now);
            } catch (Exception e) {
                LOG.warn("Could not remember the transfer of the {} documents", entry.getKey(), e);
            }
        }
    }

    /**
     * Whether one document was accepted by every target of the transfer.
     *
     * @param report the finished transfer
     * @param key    the document
     * @return true if no target rejected it
     */
    private boolean reachedEveryTarget(TransferReport report, DocumentKey key) {
        for (TargetTransferReport target : report.targets())
            for (DocumentTransferResult result : target.results())
                if (result.key().equals(key) && !result.success())
                    return false;

        return true;
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
