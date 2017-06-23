package net.anotheria.anosite.cms.user;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.anoaccessconfiguration.data.Role;
import net.anotheria.anosite.gen.anoaccessconfiguration.data.RoleBuilder;
import net.anotheria.anosite.gen.anoaccessconfiguration.service.AnoAccessConfigurationServiceException;
import net.anotheria.anosite.gen.anoaccessconfiguration.service.IAnoAccessConfigurationService;
import net.anotheria.anosite.gen.asuserdata.data.UserDef;
import net.anotheria.anosite.gen.asuserdata.data.UserDefBuilder;
import net.anotheria.anosite.gen.asuserdata.service.ASUserDataServiceException;
import net.anotheria.anosite.gen.asuserdata.service.IASUserDataService;
import net.anotheria.util.crypt.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author vbezuhlyi
 * @see CMSUser
 * @see net.anotheria.anosite.cms.action.LoginAction
 * @see net.anotheria.anosite.gen.shared.filter.CMSFilter
 * @see net.anotheria.anosite.gen.shared.action.BaseAnositeAction
 **/
public class CMSUserManager {

	/**
	 * {@link Logger} instance.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(CMSUserManager.class);

	private static Map<String, CMSUser> users;

	private static IASUserDataService userDataService;
	private static IAnoAccessConfigurationService anoAccessConfigurationService;
	private static CMSUserManager instance;
	private static boolean inited;

	private CMSUserManager() {
		if (!inited)
			throw new RuntimeException("CMS user manager is not initialized");
	}

	public static CMSUserManager getInstance() {
		if (instance == null) {
			instance = new CMSUserManager();
		}
		return instance;
	}

	public boolean canLoginUser(String login, String password) {
		CMSUser user = users.get(login);
		password = hashPassword(password);
		if (user == null) {
			return false;
		}
		return user.getPassword().equals(password);
	}

	public boolean isKnownUser(String login) {
		return users.containsKey(login);
	}

	public static void init() {
		if (inited) {
			return;
		}

		users = new ConcurrentHashMap<String, CMSUser>();
		try {
			userDataService = MetaFactory.get(IASUserDataService.class);
			anoAccessConfigurationService = MetaFactory.get(IAnoAccessConfigurationService.class);

			/* initial creating of default roles and users */
			createNecessaryDefaultRolesAndUsers();

			/* initial filling of map from CMS persistence files */
			scanUsers();

			inited = true;

		} catch (MetaFactoryException e) {
			LOGGER.error("init failed", e);
			throw new RuntimeException("MetaFactory failed", e);
		}
	}

	private static void createNecessaryDefaultRolesAndUsers() {
		try {
			/* adding each of default roles if it already not exists */
			addDefaultRole("developer");
			addDefaultRole("cmsuser");
			addDefaultRole("editor");
			addDefaultRole("producer");
			addDefaultRole("admin");

			/* adding default users */
			addDefaultUser("admin", "admin", "admin"); // + admin:admin if necessary

		} catch (ASUserDataServiceException e) {
			LOGGER.error("ASUserDataService failed", e);
			throw new RuntimeException("ASUserDataService failed", e);
		} catch (AnoAccessConfigurationServiceException e) {
			LOGGER.error("AnoAccessConfigurationService failed", e);
			throw new RuntimeException("AnoAccessConfigurationService failed", e);
		}
	}

	private static void addDefaultUser(String login, String password, String role) throws ASUserDataServiceException, AnoAccessConfigurationServiceException {
		List<UserDef> userDefs = userDataService.getUserDefsByProperty(UserDef.PROP_LOGIN, login);
		if (userDefs == null || userDefs.isEmpty()) { // check if such user does not exist
			List<Role> roleList = anoAccessConfigurationService.getRolesByProperty(Role.PROP_NAME, role);
			if (roleList == null || roleList.isEmpty()) { // check if role for admin user does not exist
				LOGGER.error("There is no admin role for admin user in CMS");
				throw new RuntimeException("admin role for admin user is undefined");
			}
			Role adminRole = roleList.get(0);

			UserDefBuilder userDefBuilder = new UserDefBuilder();
			userDefBuilder.login(login);
			userDefBuilder.password(password);
			userDefBuilder.roles(Arrays.asList(adminRole.getId()));
			userDataService.createUserDef(userDefBuilder.build());
		}
	}

	private static void addDefaultRole(String role) throws AnoAccessConfigurationServiceException {
		List<Role> roleList = anoAccessConfigurationService.getRolesByProperty(Role.PROP_NAME, role);
		if (roleList == null || roleList.isEmpty()) { // check if such role does not exist
			RoleBuilder roleBuilder = new RoleBuilder();
			roleBuilder.name(role);
			roleBuilder.permissions(Collections.<String>emptyList());
			roleBuilder.contextInitializers(Collections.<String>emptyList());
			anoAccessConfigurationService.createRole(roleBuilder.build());
		}
	}

	public static void changeUserPassword(String login, String newPassword) {
		try {
			UserDef user = getUserDefByLogin(login);
			user.setPassword(hashPassword(newPassword));
			userDataService.updateUserDef(user);
		} catch (ASUserDataServiceException e) {
			LOGGER.error("change user password failed", e);
			throw new RuntimeException("ASUserDataService failed", e);
		}
	}

	public static String hashPassword(String plainTextPassword) {
		return MD5Util.getMD5Hash(plainTextPassword) + getHashMarker();
	}

	public static String getHashMarker() {
		return "//hash";
	}

	public static String getIdByLogin(String login) {
		return getUserDefByLogin(login).getId();
	}

	public static String getLoginById(String id) {
		try {
			return userDataService.getUserDef(id).getLogin();

		} catch (ASUserDataServiceException e) {
			LOGGER.error("get user def by login failed", e);
			throw new RuntimeException("ASUserDataService failed", e);
		}
	}

	private static UserDef getUserDefByLogin(String login) {
		try {
			List<UserDef> userDefs = userDataService.getUserDefsByProperty(UserDef.PROP_LOGIN, login);
			if (userDefs.isEmpty())
				throw new RuntimeException("User with login[" + login + "] not found");

			return userDefs.get(0);
		} catch (ASUserDataServiceException e) {
			LOGGER.error("get user def by login failed", e);
			throw new RuntimeException("ASUserDataService failed", e);
		}
	}

	public static void scanUsers() {
		String username, password;
		List<String> rolesIds, roles;

		try {
			List<UserDef> usersList = userDataService.getUserDefs();
			users.clear();

			for (UserDef userDef : usersList) {
				username = userDef.getLogin();
				password = userDef.getPassword();
                rolesIds = userDef.getRoles(); // getting IDs of user roles

                roles = new ArrayList<String>();
                if (rolesIds != null && !rolesIds.isEmpty()) {
                    for (String roleId : rolesIds) {
                        Role role = anoAccessConfigurationService.getRole(roleId);
                        if (role != null) {
                            roles.add(role.getName()); // getting name of user role
                        }
                    }
                }

                CMSUser userToAdd = new CMSUser(username, password, roles);
                users.put(userToAdd.getUsername(), userToAdd);
			}
		} catch (ASUserDataServiceException e) {
			LOGGER.error("scan users failed", e);
			throw new RuntimeException("ASUserDataService failed", e);
		} catch (AnoAccessConfigurationServiceException e) {
			LOGGER.error("AnoAccessConfigurationService failed", e);
			throw new RuntimeException("AnoAccessConfigurationService failed", e);
		}
	}

}
