package net.anotheria.anosite.access.storage;

import net.anotheria.access.Role;
import net.anotheria.access.impl.MetaInfoStorage;
import net.anotheria.access.impl.SecurityBox;
import net.anotheria.access.storage.persistence.SecurityBoxPersistenceService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.anoaccessapplicationdata.data.UserData;
import net.anotheria.anosite.gen.anoaccessapplicationdata.data.UserDataBuilder;
import net.anotheria.anosite.gen.anoaccessapplicationdata.data.UserDataSortType;
import net.anotheria.anosite.gen.anoaccessapplicationdata.service.AnoAccessApplicationDataServiceException;
import net.anotheria.anosite.gen.anoaccessapplicationdata.service.IAnoAccessApplicationDataService;
import net.anotheria.util.log.LogMessageUtil;
import net.anotheria.util.sorter.SortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.List;

/**
 * File system implementation of {@link SecurityBoxPersistenceService}.
 * 
 * @author Alexandr Bolbat
 */
public class ASGSecurityBoxPersistenceServiceImpl implements SecurityBoxPersistenceService {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ASGSecurityBoxPersistenceServiceImpl.class);

	/**
	 * {@link IAnoAccessApplicationDataService} instance.
	 */
	private final IAnoAccessApplicationDataService persistence;

	/**
	 * Protected constructor.
	 */
	protected ASGSecurityBoxPersistenceServiceImpl() {
		try {
			persistence = MetaFactory.get(IAnoAccessApplicationDataService.class);
		} catch (MetaFactoryException e) {
			String message = "ASGSecurityBoxPersistenceServiceImpl() initialization fail. Can't initialize persistence service.";
			LOGGER.error(MarkerFactory.getMarker("FATAL"), message, e);
			throw new RuntimeException(message, e);
		}
	}

	@Override
	public SecurityBox create(SecurityBox t) throws CrudServiceException {
		return save(t);
	}

	@Override
	public SecurityBox read(final SaveableID boxOwner) throws CrudServiceException {
		if (boxOwner == null)
			throw new IllegalArgumentException("boxOwner argument is null");

		try {
			SortType sorting = new SortType(UserDataSortType.SORT_BY_ID, UserDataSortType.DESC);
			List<UserData> resultFromPersistence = persistence.getUserDatasByProperty(UserData.PROP_USER_ID, boxOwner.getSaveableId(), sorting);

			// if no box data for given owner
			if (resultFromPersistence == null || resultFromPersistence.isEmpty())
				throw new ItemNotFoundException(boxOwner.getSaveableId());

			// fixing not consistent data
			if (resultFromPersistence.size() > 1) {
				for (UserData toRemove : resultFromPersistence.subList(1, resultFromPersistence.size())) {
					try {
						LOGGER.warn("Removing not used data[" + toRemove + "] for box owner[" + boxOwner.getSaveableId() + "]");
						persistence.deleteUserData(toRemove);
					} catch (AnoAccessApplicationDataServiceException e) {
						LOGGER.warn("Removing fail and ignored.", e);
					}
				}
			}

			UserData resultUserData = resultFromPersistence.get(0);

			SecurityBox result = new SecurityBox();
			result.setOwnerId(resultUserData.getUserId());

			for (String roleName : resultUserData.getRoles())
				try {
					Role role = MetaInfoStorage.INSTANCE.getRole(roleName);
					if (role != null)
						result.addRole(role);
				} catch (IllegalArgumentException e) {
					LOGGER.warn("Skipping not exist user role[" + roleName + "] mapped to box owner[" + result.getOwnerId() + "].", e);
				}

			return result;
		} catch (AnoAccessApplicationDataServiceException e) {
			String message = LogMessageUtil.failMsg(e, boxOwner.getSaveableId());
			LOGGER.error(message, e);
			throw new CrudServiceException(message, e);
		}
	}

	@Override
	public SecurityBox update(final SecurityBox box) throws CrudServiceException {
		if (box == null)
			throw new IllegalArgumentException("box argument is null");

		return save(box);
	}

	@Override
	public void delete(final SecurityBox box) throws CrudServiceException {
		if (box == null)
			throw new IllegalArgumentException("box argument is null");

		try {
			SortType sorting = new SortType(UserDataSortType.SORT_BY_ID, UserDataSortType.DESC);
			List<UserData> resultFromPersistence = persistence.getUserDatasByProperty(UserData.PROP_USER_ID, box.getOwnerId(), sorting);

			// removing
			if (!resultFromPersistence.isEmpty())
				for (UserData toRemove : resultFromPersistence)
					persistence.deleteUserData(toRemove);
		} catch (AnoAccessApplicationDataServiceException e) {
			String message = LogMessageUtil.failMsg(e, box);
			LOGGER.error(message, e);
			throw new CrudServiceException(message, e);
		}
	}

	@Override
	public SecurityBox save(final SecurityBox box) throws CrudServiceException {
		if (box == null)
			throw new IllegalArgumentException("box argument is null");

		// preparing new data
		String ownerId = box.getOwnerId();
		List<String> roles = box.getOwnedRoles();
		UserData userData = new UserDataBuilder().userId(ownerId).roles(roles).build();

		try {
			// removing old data
			delete(box);
			// storing new data
			persistence.createUserData(userData);

			return box;
		} catch (AnoAccessApplicationDataServiceException e) {
			String message = LogMessageUtil.failMsg(e, box);
			LOGGER.error(message, e);
			throw new CrudServiceException(message, e);
		}
	}

	@Override
	public boolean exists(SecurityBox box) throws CrudServiceException {
		throw new UnsupportedOperationException("yet unimplemented");
	}

	@Override
	public List<SecurityBox> query(Query q) throws CrudServiceException {
		throw new UnsupportedOperationException("yet unimplemented");
	}

}
