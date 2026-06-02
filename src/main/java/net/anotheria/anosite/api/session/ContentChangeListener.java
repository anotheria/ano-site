package net.anotheria.anosite.api.session;

import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.IServiceListener;
/**
 * This is an implementation of the service listener which propagates the changes from service as events for contentchange listening apis.
 * @author another
 *
 */
public class ContentChangeListener implements IServiceListener{
	
	@Override public void documentCreated(DataObject doc) {
		sendEvent(ContentChangeType.CREATE, doc);
	}

    @Override
    public void documentImported(DataObject dataObject) {
     	sendEvent(ContentChangeType.IMPORT, dataObject);
    }

    @Override public void documentDeleted(DataObject doc) {
		sendEvent(ContentChangeType.DELETE, doc);
		
	}

	@Override public void documentUpdated(DataObject oldVersion, DataObject newVersion) {
		sendEvent(ContentChangeType.UPDATE, oldVersion);
		
	}

	@Override
	public void persistenceChanged() {
	}

	/**
	 * Sends the content change event.
	 * @param type operation
	 * @param object some document
	 */
	private void sendEvent(ContentChangeType type, DataObject object){
		throw new AssertionError("Not supported anymore!");
		//ContentChangeEvent event = new ContentChangeEvent(type, object.getDefinedName(), object.getId());
		//APISessionManager.getInstance().propagateContentChangeEvent(event);
	}

}
