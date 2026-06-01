package net.anotheria.anosite.cms.listener;

import net.anotheria.access.AccessService;
import net.anotheria.access.AccessServiceException;
import net.anotheria.access.Role;
import net.anotheria.access.SecurityObject;
import net.anotheria.access.impl.SecurityBox;
import net.anotheria.access.storage.persistence.SecurityBoxPersistenceService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.anoaccessapplicationdata.data.UserData;
import net.anotheria.anosite.gen.anoaccessconfiguration.service.IAnoAccessConfigurationService;
import net.anotheria.anosite.gen.asuserdata.data.UserDef;
import net.anotheria.anosite.gen.asuserdata.service.ASUserDataServiceException;
import net.anotheria.anosite.gen.asuserdata.service.IASUserDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.IServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks if we need to add new roles to user security box.
 *
 * @author Vlad Lukjanenko
 */
public class UserAccessGrantListener implements IServiceListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccessGrantListener.class);

    private static final IASUserDataService userDataService;

    private static final SecurityBoxPersistenceService securityBoxPersistenceService;

    private static final IAnoAccessConfigurationService accessConfigurationService;

    private static final AccessService accessService;

    static {
        try {
            userDataService = MetaFactory.get(IASUserDataService.class);
            securityBoxPersistenceService = MetaFactory.get(SecurityBoxPersistenceService.class);
            accessConfigurationService = MetaFactory.get(IAnoAccessConfigurationService.class);
            accessService = MetaFactory.get(AccessService.class);
        } catch (MetaFactoryException e) {
            LOGGER.error("MetaFactory initialize failed", e);
            throw new RuntimeException("MetaFactory initialize failed", e);
        }
    }

    @Override
    public void documentUpdated(DataObject dataObject, DataObject dataObject1) {

        if (dataObject instanceof UserData) {

            UserData userData = (UserData) dataObject;
            SecurityBox securityBox = null;
            UserDef user = null;

            try {
                user = userDataService.getUserDef(userData.getUserId());
            } catch (ASUserDataServiceException e) {
                LOGGER.error("Error occurred while getting UserDef by id", e);
                throw new RuntimeException("Error occurred while getting UserDef by id");
            }

            try {
                SaveableID saveableID = new SaveableID();
                saveableID.setSaveableId(user.getLogin());
                saveableID.setOwnerId(user.getLogin());

                securityBox = securityBoxPersistenceService.read(saveableID);
            } catch (CrudServiceException e) {
                LOGGER.warn("SecurityBox with id=" + user.getLogin() + " not found. Creating new one");
            }

            if (securityBox == null) {
                securityBox = new SecurityBox(user.getLogin());
            }

            for (String roleId : userData.getRoles()) {

                if (securityBox.hasRole(roleId)) {
                    continue;
                }

                Role role = accessService.getRole(roleId);

                try {
                    accessService.grantRole(new SecurityObject(user.getLogin()), role.getName());
                } catch (AccessServiceException e) {
                    LOGGER.error("Error occurred while granting role " + role.getName() + " to " + user.getLogin(), e);
                    throw new RuntimeException();
                }
            }
        }
    }

    @Override
    public void documentDeleted(DataObject dataObject) {

        if (dataObject instanceof UserData) {

            UserData userData = (UserData) dataObject;
            UserDef user = null;

            try {
                user = userDataService.getUserDef(userData.getUserId());
            } catch (ASUserDataServiceException e) {
                LOGGER.error("Error occurred while getting UserDef by id", e);
                throw new RuntimeException("Error occurred while getting UserDef by id");
            }

            SecurityBox securityBox = new SecurityBox(user.getLogin());

            try {
                securityBoxPersistenceService.delete(securityBox);
            } catch (CrudServiceException e) {
                LOGGER.error("Error occurred while removing SecurityBox", e);
                throw new RuntimeException("Error occurred while removing SecurityBox");
            }
        }
    }

    @Override
    public void documentCreated(DataObject dataObject) {

        if (dataObject instanceof UserData) {

            UserData userData = (UserData) dataObject;
            UserDef user = null;

            try {
                user = userDataService.getUserDef(userData.getUserId());
            } catch (ASUserDataServiceException e) {
                LOGGER.error("Error occurred while getting UserDef by id", e);
                throw new RuntimeException("Error occurred while getting UserDef by id");
            }

            SecurityBox securityBox = new SecurityBox(user.getLogin());

            try {
                securityBoxPersistenceService.create(securityBox);
            } catch (CrudServiceException e) {
                LOGGER.error("Error occurred while creating SecurityBox", e);
                throw new RuntimeException("Error occurred while creating SecurityBox");
            }
        }
    }

    @Override
    public void documentImported(DataObject dataObject) {

    }

    @Override
    public void persistenceChanged() {

    }
}
