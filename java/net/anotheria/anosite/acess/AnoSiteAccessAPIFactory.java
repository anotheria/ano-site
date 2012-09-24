package net.anotheria.anosite.acess;

import net.anotheria.anoplass.api.APIFactory;

/**
 * Factory for instantiating {@link AnoSiteAccessAPIImpl}
 * 
 * @author Alexandr Bolbat
 */
public class AnoSiteAccessAPIFactory implements APIFactory<AnoSiteAccessAPI> {

	@Override
	public AnoSiteAccessAPI createAPI() {
		return new AnoSiteAccessAPIImpl();
	}

}
