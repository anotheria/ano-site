package net.anotheria.anosite.cms.listener;

import net.anotheria.anodoc.query2.QueryNotEqualProperty;
import net.anotheria.anodoc.query2.QueryProperty;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.cms.user.CMSUserManager;
import net.anotheria.anosite.gen.asuserdata.data.UserDef;
import net.anotheria.anosite.gen.asuserdata.service.ASUserDataServiceException;
import net.anotheria.anosite.gen.asuserdata.service.IASUserDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.IServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener for checking user's updates.
 *
 * @author vbezuhlyi
 * @see CMSUserManager
 * @see IASUserDataService
 */

public class UserListener implements IServiceListener {

	/**
	 * {@link Logger} instance.
	 */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserListener.class);

    private static final IASUserDataService userDataService;

    static {
        try {
            userDataService = MetaFactory.get(IASUserDataService.class);
        } catch (MetaFactoryException e) {
            LOGGER.error("MetaFactory for UserListener failed", e);
            throw new RuntimeException("MetaFactory for UserListener failed", e);
        }
    }

    @Override
    public void documentUpdated(DataObject dataObject, DataObject dataObject1) {
        if (dataObject instanceof UserDef)
            updateUser(dataObject, dataObject1);
    }

    @Override
    public void documentDeleted(DataObject dataObject) {

    }

    @Override
    public void documentCreated(DataObject dataObject) {
        if (dataObject instanceof UserDef)
            updateUser(null, dataObject);
    }

    @Override
    public void documentImported(DataObject dataObject) {
        if (dataObject instanceof UserDef)
            updateUser(null, dataObject);
    }

    @Override
    public void persistenceChanged() {
    }

    private void updateUser(DataObject old, DataObject updated) {
        UserDef userDef = (UserDef) old;
        UserDef userDefNew = (UserDef) updated;

        boolean shouldUpdate = false;

        if (userDef != null && isDifferent(userDef.getLogin(), userDefNew.getLogin()) && isTryingToCreateExistedLogin(userDef)) {
            userDefNew.setLogin(userDefNew.getLogin() + userDefNew.getId()); // adding ID to login if such login already exists
            shouldUpdate = true;
        }

        if (isDifferent(userDef != null ? userDef.getPassword() : null, userDefNew.getPassword()) || !userDefNew.getPassword().contains(CMSUserManager.getHashMarker())) {
            userDefNew.setPassword(CMSUserManager.hashPassword(userDefNew.getPassword())); // hashing new or changed plain-text password
            shouldUpdate = true;
        }

        if (shouldUpdate)
            try {
                userDataService.updateUserDef(userDefNew);
            } catch (ASUserDataServiceException e) {
                LOGGER.error("ASUserDataService for UserListener failed", e);
                throw new RuntimeException("ASUserDataService for UserListener failed", e);
            }

        CMSUserManager.scanUsers(); // updating info as user probably has new login or password


    }

    private <T> boolean isDifferent(T obj1, T obj2) {
        return obj1 != null && !obj1.equals(obj2) || obj2 != null && !obj2.equals(obj1);
    }

    private boolean isTryingToCreateExistedLogin(UserDef userDef) {
        try {
            List<QueryProperty> props = new ArrayList<QueryProperty>();
            props.add(new QueryProperty(UserDef.PROP_LOGIN, userDef.getLogin()));
            props.add(new QueryNotEqualProperty(UserDef.PROP_ID, userDef.getId()));
            List<UserDef> existedUserDefs = userDataService.getUserDefsByProperty(props.toArray(new QueryProperty[props.size()]));

            return existedUserDefs != null && !existedUserDefs.isEmpty();

        } catch (ASUserDataServiceException e) {
            LOGGER.error("ASUserDataService for UserListener failed", e);
            throw new RuntimeException("ASUserDataService for UserListener failed", e);
        }
    }

}
