package net.anotheria.anosite.bredcrambs.action;

import net.anotheria.anosite.bredcrambs.data.DocumentEnum;
import net.anotheria.asg.exception.ConstantNotFoundException;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.json.JSONResponse;
import net.anotheria.util.StringUtils;
import net.anotheria.webutils.actions.BaseAction;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Action shows usages of specific element in cms.
 *
 * @author vzarva
 */
public class ShowUsagesOfDocumentAction extends BaseAction {

    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowUsagesOfDocumentAction.class);

    /**
     * Encoding.
     */
    private static final String UTF_8 = "UTF-8";
    /**
     * Content type.
     */
    private static final String TEXT_X_JSON = "application/json";

    /**
     * Type of searched element.
     */
    private static final String DOC_PARAM = "doc";

    /**
     * Id of searched element.
     */
    private static final String SEARCHED_ELEMENT_ID_PARAM = "pId";

    /**
     * References to usages of current document.
     */
    private static final String REFERENCES_ATTR = "references";


    @Override
    public ActionCommand execute(ActionMapping mapping,  HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONResponse response = new JSONResponse();
        String doc = req.getParameter(DOC_PARAM);
        String pId = req.getParameter(SEARCHED_ELEMENT_ID_PARAM);

        if (StringUtils.isEmpty(doc) || StringUtils.isEmpty(pId)){
            LOGGER.warn("Incoming parameters doc[" + doc + "] or pId[" + pId + "] is empty");
            response.addError("Incoming parameters doc[" + doc + "] or pId[" + pId + "] is empty");
            writeTextToResponse(res, response);
            return null;
        }
        List<String> references = new ArrayList<String>();
        try {
            DocumentEnum DOC = DocumentEnum.getConstantByValue(doc);
            references = DOC.findReferences(pId);
        } catch (ConstantNotFoundException e) {
            LOGGER.info("Usages for document["+doc+"] is not implemented",e);
            response.addError("Usages for document["+doc+"] is not implemented");
            writeTextToResponse(res,response);
            return null;
        }
        JSONObject jsonReferences = new JSONObject();
        jsonReferences.put(REFERENCES_ATTR,references);
        response.setData(jsonReferences);

        writeTextToResponse(res,response);
        return null;
    }

    /**
     * Writes specified text to response and flushes the stream.
     *
     * @param res
     *            {@link jakarta.servlet.http.HttpServletRequest}
     * @param jsonResponse
     *            {@link net.anotheria.maf.json.JSONResponse}
     * @throws java.io.IOException
     *             if an input or output exception occurred
     * @throws org.json.JSONException
     *             on JSON create errors
     */
    private void writeTextToResponse(final HttpServletResponse res, final JSONResponse jsonResponse) throws IOException, JSONException {
        res.setCharacterEncoding(UTF_8);
        res.setContentType(TEXT_X_JSON);
        PrintWriter writer = res.getWriter();
        writer.write(jsonResponse.toString());
        writer.flush();
    }


}
