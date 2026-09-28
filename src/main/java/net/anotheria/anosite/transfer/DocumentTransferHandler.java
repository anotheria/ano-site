package net.anotheria.anosite.transfer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.json.JSONResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The request end of the transfer, shared by every generated transfer action.
 *
 * <p>The generated {@code Transfer<Document>Action} does nothing but name its document and call
 * {@link #handle(HttpServletRequest, HttpServletResponse, String, String)} — reading the request, running the
 * transfer and writing the answer is the same work for all of them, so it lives here instead of in every
 * generated class.
 *
 * <p>The answer is a {@link JSONResponse}: {@code data} carries the report the dialog renders, {@code errors}
 * carries what went wrong, so a client can keep checking {@code errors} alone and be right.
 *
 * @author Leon Rosenberg
 */
public final class DocumentTransferHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentTransferHandler.class);

    /**
     * Request parameter carrying the id of the document to transfer.
     */
    public static final String PARAM_DOCUMENT_ID = "pId";

    /**
     * Request parameter carrying the name of the target group, as configured in anositeconfig.
     */
    public static final String PARAM_TARGET_GROUP = "transferTarget";

    /**
     * Request parameter carrying the transfer mode, see {@link TransferMode#fromParameter(String)}.
     */
    public static final String PARAM_MODE = "transferMode";

    /**
     * Error field the messages are reported under.
     */
    private static final String ERROR = "error";

    private DocumentTransferHandler() {
    }

    /**
     * Runs the transfer the request asks for and writes the report.
     *
     * @param req          the request, carrying document id, target group and mode
     * @param res          the response, receives the json report
     * @param moduleName   module of the document, generated into the calling action
     * @param documentName document type, generated into the calling action
     * @return null, the answer is written directly
     * @throws Exception if the response cannot be written
     */
    public static ActionCommand handle(HttpServletRequest req, HttpServletResponse res, String moduleName,
                                       String documentName) throws Exception {
        JSONResponse response = new JSONResponse();

        String id = req.getParameter(PARAM_DOCUMENT_ID);
        if (id == null || id.trim().isEmpty()) {
            response.addError(ERROR, "No document id submitted.");
            write(res, response);
            return null;
        }

        String groupName = req.getParameter(PARAM_TARGET_GROUP);
        TransferMode mode = TransferMode.fromParameter(req.getParameter(PARAM_MODE));
        DocumentKey key = new DocumentKey(moduleName, documentName, id.trim());

        try {
            TransferReport report = new DocumentTransferService().transfer(key, mode, groupName);
            response.setData(toJSON(report));
            if (!report.isSuccess())
                for (String error : collectErrors(report))
                    response.addError(ERROR, error);
        } catch (DocumentTransferException e) {
            LOG.warn("Transfer of {} to group {} was refused", key, groupName, e);
            response.addError(ERROR, e.getMessage());
        } catch (Exception e) {
            LOG.error("Unexpected failure transferring {} to group {}", key, groupName, e);
            response.addError(ERROR, "Transfer failed: " + e.getMessage());
        }

        write(res, response);
        return null;
    }

    /**
     * Turns the report into the object the transfer dialog renders.
     *
     * @param report report of a finished transfer
     * @return json form of the report
     * @throws JSONException if the report cannot be written as json
     */
    private static JSONObject toJSON(TransferReport report) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("success", report.isSuccess());
        data.put("group", report.groupName());
        data.put("mode", report.mode().getParameterValue());
        data.put("documentCount", report.documents().size());

        JSONArray documents = new JSONArray();
        for (DocumentKey document : report.documents())
            documents.put(document.toString());
        data.put("documents", documents);

        JSONArray targets = new JSONArray();
        for (TargetTransferReport target : report.targets()) {
            JSONObject targetJson = new JSONObject();
            targetJson.put("name", target.targetName());
            targetJson.put("url", target.url());
            targetJson.put("success", target.isSuccess());
            targetJson.put("succeeded", target.succeeded());
            targetJson.put("failed", target.failed());

            JSONArray errors = new JSONArray();
            for (DocumentTransferResult result : target.results())
                if (!result.success())
                    errors.put(result.key() + ": " + result.message());
            targetJson.put("errors", errors);

            targets.put(targetJson);
        }
        data.put("targets", targets);

        JSONArray warnings = new JSONArray();
        for (String warning : report.warnings())
            warnings.put(warning);
        data.put("warnings", warnings);

        return data;
    }

    /**
     * One error line per failed target, short enough to show in a notification.
     *
     * @param report report of a finished transfer
     * @return error messages, empty if everything worked
     */
    private static List<String> collectErrors(TransferReport report) {
        List<String> errors = new ArrayList<>();
        if (report.targets().isEmpty())
            errors.add("Group " + report.groupName() + " has no usable target, nothing was transferred.");

        for (TargetTransferReport target : report.targets()) {
            if (target.isSuccess())
                continue;

            String first = "";
            for (DocumentTransferResult result : target.results())
                if (!result.success()) {
                    first = " First failure: " + result.key() + " - " + result.message();
                    break;
                }

            errors.add("Transfer to " + target.targetName() + " (" + target.url() + ") failed for "
                    + target.failed() + " of " + target.results().size() + " document(s)." + first);
        }
        return errors;
    }

    /**
     * Writes a json response.
     *
     * @param res      the response
     * @param response what to write
     * @throws Exception if the response cannot be written
     */
    private static void write(HttpServletResponse res, JSONResponse response) throws Exception {
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        PrintWriter writer = res.getWriter();
        writer.write(response.toString());
        writer.flush();
    }
}
