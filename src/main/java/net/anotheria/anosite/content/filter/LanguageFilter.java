package net.anotheria.anosite.content.filter;

import static net.anotheria.anosite.util.AnositeConstants.PARAM_LANGUAGE;
import static net.anotheria.anosite.util.AnositeConstants.SA_LANGUAGE;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import net.anotheria.anodoc.util.context.BrandConfig;
import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asbrand.data.Brand;
import net.anotheria.anosite.gen.asbrand.service.ASBrandServiceException;
import net.anotheria.anosite.gen.asbrand.service.IASBrandService;
import net.anotheria.anosite.gen.shared.service.AnoDocConfigurator;
import net.anotheria.asg.exception.ASGRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import static net.anotheria.anosite.util.AnositeConstants.*;
import static net.anotheria.anosite.util.AnositeConstants.SA_BRAND;

public class LanguageFilter implements Filter{
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LanguageFilter.class);
	/**
	 * {@link IASBrandService} instance.
	 */
	private IASBrandService brandService;

	@Override public void destroy() {
	}

	@Override public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
		if (!(rq instanceof HttpServletRequest)){
			chain.doFilter(rq, rs);
			return;
		}
		
	
		HttpServletRequest req = (HttpServletRequest)rq;
		req.setCharacterEncoding(AnoDocConfigurator.getEncoding());
		HttpSession session = req.getSession();
		
		String pLang = req.getParameter(PARAM_LANGUAGE); 
		if (pLang!=null){
			session.setAttribute(SA_LANGUAGE, pLang);
		}
		
		String lang = (String)session.getAttribute(SA_LANGUAGE);
		if (lang != null){
			ContextManager.getCallContext().setCurrentLanguage(lang);
			//System.out.println("Setting current language to : "+lang);
		}else{
			lang = ContextManager.getCallContext().getCurrentLanguage();
		}
		
		Locale toSet = new Locale(lang);
		//TODO remove this?
		//session.setAttribute(Action.LOCALE_KEY, toSet);
		session.setAttribute(SA_LANGUAGE, lang);

		prepareBrand(req);

		chain.doFilter(rq, rs);
	}

	private void prepareBrand(HttpServletRequest req) {
		BrandConfig brandConfig = ContextManager.getCallContext().getBrandConfig();
		if (brandConfig != null && brandConfig.getUrlsToMap().contains(req.getServerName())) {
			req.getSession().setAttribute(SA_BRAND, brandConfig.getName());
			return;
		}

		Brand brand = null;
		try {
			List<Brand> brands = brandService.getBrands();
			for (Brand b: brands)
				if (b.getUrlsToMap().contains(req.getServerName())) {
					brand = b;
					break;
				}
		} catch (ASBrandServiceException e) {
		}

		//trying to get default brand
		if (brand == null) {
			try {
				List<Brand> brands = brandService.getBrandsByProperty(Brand.PROP_DEFAULT_BRAND, true);
				if (brands == null || brands.size() == 0) {
					LOGGER.warn("Default brand not found");
				} else if (brands.size() > 1){
					LOGGER.warn("Default brand more than 1.");
				} else {
					brand = brands.get(0);
				}
			} catch (ASBrandServiceException e) {
				LOGGER.error("Unable to get default brand. {}", e.getMessage());
			}
		}

		if (brand == null) {
			LOGGER.error("Brand is null for " + req.getServerName());
			return;
		}

		brandConfig = new BrandConfig(brand.getName(), brand.getDefaultBrand(), brand.getUrlsToMap(), brand.getLocalizations(), brand.getMediaLinks(), brand.getAttributes());
		ContextManager.getCallContext().setBrandConfig(brandConfig);

		req.getSession().setAttribute(SA_BRAND, brandConfig.getName());
	}

	@Override public void init(FilterConfig arg0) throws ServletException {
		AnoDocConfigurator.configure();
		try {
			brandService = MetaFactory.get(IASBrandService.class);
		} catch (MetaFactoryException e){
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "ASG services init failure", e);
			throw new ServletException("ASG services init failure",e);
		}
	}
}
