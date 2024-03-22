package net.anotheria.anosite.api.feature;

import net.anotheria.anodoc.util.context.BrandConfig;
import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asbrand.data.Brand;
import net.anotheria.anosite.gen.asbrand.service.ASBrandServiceException;
import net.anotheria.anosite.gen.asbrand.service.IASBrandService;
import net.anotheria.anosite.gen.asfeature.data.BrandFeature;
import net.anotheria.anosite.gen.asfeature.data.Feature;
import net.anotheria.anosite.gen.asfeature.service.ASFeatureServiceException;
import net.anotheria.anosite.gen.asfeature.service.IASFeatureService;
import net.anotheria.anosite.guard.ConditionalGuard;
import net.anotheria.anosite.guard.GuardFactory;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

import java.util.List;

/**
 * {@link FeatureAPI} implementation.
 *
 * @author lrosenberg
 * @since 12.07.14 20:40
 */
public class FeatureAPIImpl extends AbstractAPIImpl implements FeatureAPI {
	/**
	 * {@link IASFeatureService} instance.
	 */
	private IASFeatureService featureService;
	/**
	 * {@link IASBrandService} instance.
	 */
	private IASBrandService brandService;

	@Override
	public boolean isFeatureActive(String name) throws APIException {
		return isSimpleFeature(name) || isBrandFeature(name);
	}

	private boolean isSimpleFeature(String name) throws APIException {
		try {
			List<Feature> featuresList = featureService.getFeaturesByProperty(Feature.PROP_NAME, name);
			if (featuresList == null || featuresList.size() == 0){
				return false;
			}

			if (featuresList.size() > 1) {
				log.warn("Found more than 1 feature with name " + name);
				return false;
			}

			Feature f = featuresList.get(0);
			if (!f.getEnabled())
				return false;

			/* check if this feature must be worked on production */
			if (getCurrentSystem().startsWith("PROD") && !f.getActiveInProduction())
				return false;

			/* if feature is marked as 'obsolete' and it is being used */
			if (f.getObsolete()) {
				log.warn("Feature " + f.getName() + " is being used, though it is marked as 'OBSOLETE'");
			}

			return processByGuards(f.getGuards(), "Feature: " + name);
		}catch(ASFeatureServiceException e){
			log.warn("Feature " + name + " not found, due ", e);
			return false;
		}
	}

	private boolean isBrandFeature(String name) throws APIException {
		try {
			List<BrandFeature> brandFeatureList = featureService.getBrandFeaturesByProperty(BrandFeature.PROP_NAME, name);
			if (brandFeatureList == null || brandFeatureList.size() == 0) {
				return false;
			}

			if (brandFeatureList.size() > 1) {
				log.warn("Found more than 1 brand feature with name " + name);
				return false;
			}

			BrandFeature brandFeature = brandFeatureList.get(0);
			if (!brandFeature.getEnabled())
				return false;

			/* check if this feature must be worked on production */
			if (getCurrentSystem().startsWith("PROD") && !brandFeature.getActiveInProduction())
				return false;

			/* if feature is marked as 'obsolete' and it is being used */
			if (brandFeature.getObsolete()) {
				log.warn("Brand feature " + brandFeature.getName() + " is being used, though it is marked as 'OBSOLETE'");
			}

			List<String> brandIds = brandFeature.getBrands();
			if (brandIds == null || brandIds.size() == 0)
				return true;

			BrandConfig brandConfig = ContextManager.getCallContext().getBrandConfig();
			boolean isValidateByBrand = true;
			if (brandConfig != null) {
				isValidateByBrand = false;
				for (String brandId: brandIds) {
					try {
						Brand brand = brandService.getBrand(brandId);
						if (brand.getName().equals(brandConfig.getName())) {
							isValidateByBrand = true;
							break;
						}
					} catch (ASBrandServiceException e) {
						log.warn("Unable to check brand {} for brand feature {}. {}", brandId, name, e.getMessage());
					}
				}
			}
			return isValidateByBrand && processByGuards(brandFeature.getGuards(), "Brand feature: " + name);
		} catch (ASFeatureServiceException e) {
			log.warn("Brand feature " + name + " not found, due ", e);
			return false;
		}
	}

	private boolean processByGuards(List<String> gIds, String featureDataStr) {
		if (gIds==null || gIds.size()==0)
			return true;

		for (String gid : gIds) {
			ConditionalGuard g = null;
			try {
				g = GuardFactory.getConditionalGuard(gid);
				if (!g.isConditionFullfilled(null, null)) {
					return false;
				}

			} catch (Exception e) {
				log.warn("Caught error in guard processing(" + featureDataStr + ", guard: " + g + ", gid: " + gid + ")", e);
			}
		}
		return true;
	}

	@Override
	public void init() throws APIInitException {
		try{
			featureService = MetaFactory.get(IASFeatureService.class);
			brandService = MetaFactory.get(IASBrandService.class);
		}catch(MetaFactoryException e){
			throw new APIInitException("Feature service not found.");
		}
	}

	@Override
	public void deInit() {
		featureService = null;
		brandService = null;
	}

	private String getCurrentSystem() {
		Environment environment = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		Environment resultEnviroment = environment;
		while(environment.isReduceable() && (environment = environment.reduce()) != GlobalEnvironment.INSTANCE) {
			resultEnviroment = environment;
		}
		return resultEnviroment.expandedStringForm().toUpperCase();
	}
}
