package net.anotheria.anosite.action.servlet.cms;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.action.Action;
import net.anotheria.anosite.action.ActionProducer;
import net.anotheria.anosite.action.ActionWrapper;
import net.anotheria.anosite.gen.asaction.data.ActionDef;
import net.anotheria.anosite.gen.asaction.service.IASActionService;
import net.anotheria.anosite.gen.ascustomaction.data.ActionMappingDef;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility  for Action creation.
 */
public class ActionFactory {

	/**
	 * {@link Logger} instance.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(ActionFactory.class);

	/**
	 * {@link IASActionService} CMS service instance.
	 */
	private static IASActionService service;

	/**
	 * Local storage for ActionProducer.
	 */
	private static Map<String, ActionProducer> producers = new ConcurrentHashMap<String, ActionProducer>();

	/**
	 * {@link IdBasedLockManager} instance.
	 */
	private static final IdBasedLockManager lockManager = new SafeIdBasedLockManager();

	/**
	 * Init.
	 */
	static {
		try {
			service = MetaFactory.get(IASActionService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "IASActionService init failure", e);
		}
	}

	/**
	 * Creates and return required Action.
	 *
	 * @param def {@link ActionMappingDef} default mapping  from cms
	 * @return {@link Action} as result
	 */
	public static Action createAction(ActionMappingDef def) {
		String actionId = def.getAction();

		try {
			ActionDef ad = service.getActionDef(actionId);
			String clazz = ad.getClazz();

			// Action - moskito producer lookUp
			String producerId = clazz + "-" + ad.getId();
			ActionProducer producer = producers.get(producerId);

			if (producer == null) {
				IdBasedLock lock = lockManager.obtainLock(producerId);
				try {
					lock.lock();
					producer = new ActionProducer(producerId);
					producers.put(producerId, producer);
				} finally {
					lock.unlock();
				}
			}

			return new ActionWrapper((Action) Class.forName(clazz).newInstance(), producer);
		} catch (Exception e) {
			LOGGER.error("createAction(" + def + ")", e);
		}

		return null;

	}

}
