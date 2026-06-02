package net.anotheria.anosite.cms.listener;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.IServiceListener;
import net.anotheria.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simplest Listener - for Logging CRUD operations.
 *
 * @author h3llka
 */

public class CRUDLogListener implements IServiceListener {

	/**
	 * {@link Logger} instance.
	 */
    private static final Logger LOGGER = LoggerFactory.getLogger("cms-crud-log");

	/**
	 * Separator.
	 */
    private static final String SEPARATOR = " , ";

    @Override
    public void documentUpdated(DataObject dataObject, DataObject dataObject1) {
        logData("UPDATE", createObjString(dataObject, dataObject1));
    }

    @Override
    public void documentDeleted(DataObject dataObject) {
        logData("DELETE", createObjString(dataObject));
    }

    @Override
    public void documentCreated(DataObject dataObject) {
        logData("CREATE", createObjString(dataObject));
    }

    @Override
    public void documentImported(DataObject dataObject) {
        logData("IMPORT", createObjString(dataObject));
    }

	@Override
	public void persistenceChanged() {
		//System.out.println("Persistence changed");
		logData("PERSISTENCE CHANGED", null);
	}

	/**
     * Just creates string with doc.id && clazz name
     *
     * @param dataObjects objects array
     * @return created string
     */
    private String createObjString(DataObject... dataObjects) {
        String result = "";
        int count = 0;
        for (DataObject obj : dataObjects) {
            result += count == 0 ? " DOCUMENT [ id : " + obj.getId() + " ] " :
                    " DOCUMENT" + count + " [ id : " + obj.getId() + " ] ";
            count++;
        }
        result += " --> class(zz) : " + dataObjects[0].getClass().toString().substring(dataObjects[0].getClass().toString().lastIndexOf(".") + 1);
        return result;
    }

    /**
     * Writing data to log
     *
     * @param operation - CREATE - UPDATE - DELETE etc.
     * @param objData   object info
     */
    private void logData(String operation, String objData) {
        String user = APICallContext.getCallContext().getCurrentUserId();
        String editor = APICallContext.getCallContext().getCurrentEditorId();
        String time = NumberUtils.makeISO8601TimestampString(System.currentTimeMillis());
        LOGGER.info(time + SEPARATOR + operation + SEPARATOR + " uid : " + user + SEPARATOR + "eid : " + editor + SEPARATOR + objData);
    }
}
