package net.anotheria.anosite.api.feature;

import net.anotheria.anoplass.api.APIFactory;

/**
 * Factory for {@link FeatureAPI}.
 *
 * @author lrosenberg
 * @since 12.07.14 20:40
 */
public class FeatureAPIFactory implements APIFactory<FeatureAPI> {
	@Override
	public FeatureAPI createAPI() {
		return new FeatureAPIImpl();
	}
}
