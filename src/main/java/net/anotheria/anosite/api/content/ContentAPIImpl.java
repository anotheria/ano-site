package net.anotheria.anosite.api.content;

import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.IServiceListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ContentAPI} implementation.
 * Parsed localization bundles are cached per language. The whole cache is dropped
 * whenever anything in the resource data service changes.
 *
 * @author another
 *
 */
public class ContentAPIImpl extends AbstractAPIImpl implements ContentAPI{

	/**
	 * {@link IASResourceDataService} instance.
	 */
	private IASResourceDataService resourceDataService;
	/**
	 * Listener that drops the cache on changes in the resource data service.
	 */
	private final IServiceListener cacheInvalidationListener = new CacheInvalidationListener();
	/**
	 * Parsed bundles by language. Replaced as a whole on invalidation, so a snapshot that
	 * was being built while a change happened ends up in the discarded map.
	 */
	private volatile Map<String, LocalizationSnapshot> localizationCache = new ConcurrentHashMap<>();

	@Override public String getLocalizedValue(String key) throws APIException {
		return getLocalizationSnapshot().anyBundle().get(key);
	}

	@Override public String getLocalizedValue(String bundleIdOrName, String key) throws APIException {
		LocalizationSnapshot snapshot = getLocalizationSnapshot();
		Map<String, String> bundle = snapshot.byId().get(bundleIdOrName);
		if (bundle == null) {
			String id = snapshot.idByName().get(bundleIdOrName);
			bundle = id == null ? null : snapshot.byId().get(id);
		}
		if (bundle == null) {
			log.warn("Localization bundle " + bundleIdOrName + " not found");
			return null;
		}
		return bundle.get(key);
	}

	private LocalizationSnapshot getLocalizationSnapshot() throws APIException {
		Map<String, LocalizationSnapshot> cache = localizationCache;
		String language = ContextManager.getCallContext().getCurrentLanguage();
		LocalizationSnapshot snapshot = cache.get(language);
		if (snapshot != null)
			return snapshot;
		snapshot = loadLocalizationSnapshot();
		LocalizationSnapshot existing = cache.putIfAbsent(language, snapshot);
		return existing != null ? existing : snapshot;
	}

	/**
	 * Loads and parses all bundles in the current language.
	 */
	private LocalizationSnapshot loadLocalizationSnapshot() throws APIException {
		List<LocalizationBundle> bundles;
		try {
			bundles = resourceDataService.getLocalizationBundles();
		} catch (ASResourceDataServiceException e) {
			throw new APIException("Unable to load localization bundles", e);
		}

		Map<String, Map<String, String>> ownMessages = new HashMap<>();
		Map<String, String> parents = new HashMap<>();
		Map<String, String> idByName = new HashMap<>();
		Map<String, String> anyBundle = new HashMap<>();
		for (LocalizationBundle bundle : bundles) {
			Map<String, String> messages = parse(bundle.getMessages());
			ownMessages.put(bundle.getId(), messages);
			parents.put(bundle.getId(), bundle.getParentBundle());
			if (bundle.getName() != null && idByName.putIfAbsent(bundle.getName(), bundle.getId()) != null)
				log.warn("Duplicate localization bundle name " + bundle.getName() + ", lookup by name uses bundle " + idByName.get(bundle.getName()));
			for (Map.Entry<String, String> e : messages.entrySet())
				anyBundle.putIfAbsent(e.getKey(), e.getValue());
		}

		Map<String, Map<String, String>> byId = new HashMap<>();
		for (String id : ownMessages.keySet())
			byId.put(id, Collections.unmodifiableMap(withParents(id, ownMessages, parents)));

		return new LocalizationSnapshot(
				Collections.unmodifiableMap(byId),
				Collections.unmodifiableMap(idByName),
				Collections.unmodifiableMap(anyBundle));
	}

	/**
	 * Merges a bundle with its parent chain, the bundle's own keys win.
	 */
	private Map<String, String> withParents(String id, Map<String, Map<String, String>> ownMessages, Map<String, String> parents) {
		Map<String, String> result = new HashMap<>();
		Set<String> visited = new HashSet<>();
		for (String current = id; current != null && !current.isEmpty(); current = parents.get(current)) {
			if (!visited.add(current)) {
				log.warn("Cycle in parent chain of localization bundle " + id);
				break;
			}
			Map<String, String> messages = ownMessages.get(current);
			if (messages == null) {
				log.warn("Parent bundle " + current + " of localization bundle " + id + " not found");
				break;
			}
			for (Map.Entry<String, String> e : messages.entrySet())
				result.putIfAbsent(e.getKey(), e.getValue());
		}
		return result;
	}

	/**
	 * Parses bundle messages: one key=value per line, the key is everything before the first '=',
	 * the value the rest of the line verbatim; a line without a key continues the previous value.
	 * Same format as the CMS import/export and the MCP localization tools.
	 */
	static Map<String, String> parse(String content) {
		Map<String, String> result = new LinkedHashMap<>();
		if (content == null || content.isBlank())
			return result;
		String lastKey = null;
		for (String line : content.replace("\r", "").split("\n", -1)) {
			if (line.isBlank())
				continue;
			int sep = line.indexOf('=');
			if (sep <= 0) {
				if (lastKey != null)
					result.put(lastKey, result.get(lastKey) + "\n" + line);
				continue;
			}
			String key = line.substring(0, sep);
			result.put(key, line.substring(sep + 1));
			lastKey = key;
		}
		return result;
	}

	private void invalidateLocalizationCache() {
		localizationCache = new ConcurrentHashMap<>();
	}

	@Override public void init() throws APIInitException {
		super.init();
		try {
			resourceDataService = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			throw new APIInitException("Resource data service not found.");
		}
		resourceDataService.addServiceListener(cacheInvalidationListener);
	}

	@Override public void deInit() {
		if (resourceDataService != null)
			resourceDataService.removeServiceListener(cacheInvalidationListener);
		resourceDataService = null;
		invalidateLocalizationCache();
		super.deInit();
	}

	/**
	 * Parsed localization bundles of one language.
	 *
	 * @param byId		bundle id -> messages including inherited parent messages
	 * @param idByName	bundle name -> bundle id
	 * @param anyBundle	key -> value from the first bundle defining the key
	 */
	private record LocalizationSnapshot(Map<String, Map<String, String>> byId, Map<String, String> idByName, Map<String, String> anyBundle) {}

	/**
	 * Drops the localization cache on any change in the resource data service.
	 */
	private final class CacheInvalidationListener implements IServiceListener {

		@Override public void documentUpdated(DataObject oldVersion, DataObject newVersion) {
			invalidateLocalizationCache();
		}

		@Override public void documentDeleted(DataObject doc) {
			invalidateLocalizationCache();
		}

		@Override public void documentCreated(DataObject doc) {
			invalidateLocalizationCache();
		}

		@Override public void documentImported(DataObject doc) {
			invalidateLocalizationCache();
		}

		@Override public void persistenceChanged() {
			invalidateLocalizationCache();
		}
	}
}
