package net.anotheria.anosite.cms.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.anosite.gen.shared.action.BaseToolsAction;
import net.anotheria.anosite.shared.AnositeConfig;
import net.anotheria.anosite.transfer.TransferMode;
import net.anotheria.anosite.transfer.TransferTarget;
import net.anotheria.anosite.transfer.TransferTargetGroup;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.json.JSONResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;

/**
 * Answers what this instance can transfer to, so the transfer dialog can offer it.
 *
 * <p>The dialog asks once, when it opens, instead of having the target list generated into every jsp — targets
 * are configuration and change without a regeneration.
 *
 * @author Leon Rosenberg
 */
public class TransferTargetsAction extends BaseToolsAction {

    @Override
    protected String getTitle() {
        return "";
    }

    @Override
    protected String getCurrentModuleDefName() {
        return "";
    }

    @Override
    protected String getCurrentDocumentDefName() {
        return "";
    }

    @Override
    public ActionCommand anoDocExecute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
        AnositeConfig config = AnositeConfig.getInstance();

        JSONObject data = new JSONObject();
        data.put("enabled", config.isTransferEnabled());

        JSONArray groups = new JSONArray();
        for (TransferTargetGroup group : config.getUsableTransferTargetGroups()) {
            JSONObject groupJson = new JSONObject();
            groupJson.put("name", group.getName());
            //an editor should know which group is kept in sync by itself before transferring into it by hand.
            groupJson.put("autoTransfer", group.isAutoTransfer());

            JSONArray targets = new JSONArray();
            for (TransferTarget target : group.getTargets())
                if (target != null && target.isValid()) {
                    JSONObject targetJson = new JSONObject();
                    targetJson.put("name", target.getName() == null ? target.getNormalizedUrl() : target.getName());
                    targetJson.put("url", target.getNormalizedUrl());
                    targets.put(targetJson);
                }
            groupJson.put("targets", targets);

            groups.put(groupJson);
        }
        data.put("groups", groups);

        JSONArray modes = new JSONArray();
        for (TransferMode mode : TransferMode.values()) {
            JSONObject modeJson = new JSONObject();
            modeJson.put("value", mode.getParameterValue());
            modeJson.put("label", mode.getLabel());
            modes.put(modeJson);
        }
        data.put("modes", modes);

        JSONResponse response = new JSONResponse();
        response.setData(data);
        if (!config.isTransferEnabled())
            response.addError("error", "Transfer is not enabled on this instance.");
        else if (groups.length() == 0)
            response.addError("error", "No transfer target groups are configured.");

        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        PrintWriter writer = res.getWriter();
        writer.write(response.toString());
        writer.flush();

        return null;
    }
}
