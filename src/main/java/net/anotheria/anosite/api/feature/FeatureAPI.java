package net.anotheria.anosite.api.feature;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

/**
 * FeatureAPI for working with features.
 *
 * @author lrosenberg
 * @since 12.07.14 20:40
 */
public interface FeatureAPI extends API{
	/**
	 * Checks if feature with specified name is active.
	 *
	 * @param name			name of feature
	 * @return				{@code true} if feature is active, {@code false} - otherwise
	 * @throws APIException	if any errors occurs
	 */
	boolean isFeatureActive(String name) throws APIException;
}
