package net.anotheria.anosite.api.content;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

/**
 * API for accessing site content.
 * @author another
 *
 */
public interface ContentAPI extends API{

	/**
	 * Returns the value for the given key from any localization bundle, in the current language.
	 * If more than one bundle contains the key, the bundle listed first by the resource data service wins.
	 *
	 * @param key			message key
	 * @return				the value, or {@code null} if no bundle contains the key
	 * @throws APIException	if bundles can't be loaded
	 */
	String getLocalizedValue(String key) throws APIException;

	/**
	 * Returns the value for the given key from the given localization bundle, in the current language.
	 * Keys the bundle doesn't define are inherited from its parent bundle(s).
	 *
	 * @param bundleIdOrName	id or name of the bundle; the id is tried first
	 * @param key				message key
	 * @return					the value, or {@code null} if the bundle doesn't exist or doesn't contain the key
	 * @throws APIException		if bundles can't be loaded
	 */
	String getLocalizedValue(String bundleIdOrName, String key) throws APIException;
}
