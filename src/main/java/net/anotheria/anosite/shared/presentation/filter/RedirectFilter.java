package net.anotheria.anosite.shared.presentation.filter;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.assitedata.data.RedirectUrl;
import net.anotheria.anosite.gen.assitedata.service.ASSiteDataServiceException;
import net.anotheria.anosite.gen.assitedata.service.IASSiteDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * This filter performs ano-site feature of redirecting. Redirects are stored in IASSiteDataService.redirectUrls.
 * @author another
 *
 */

public class RedirectFilter implements Filter{
	private IASSiteDataService siteDataService;
	
	private static Logger log = LoggerFactory.getLogger(RedirectFilter.class);
    private static final String CHAR = "/";

    @Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sres,	FilterChain chain) throws IOException, ServletException {
		if (!(sreq instanceof HttpServletRequest))
			return;
		HttpServletRequest req = (HttpServletRequest)sreq;
		
		//build url.
		String url = req.getRequestURL().toString();
		String qs = req.getQueryString();
		if (qs!=null && qs.length()>0)
			url += qs;
		
		String redirectUrl = resolveRedirectUrl(url);
		if (redirectUrl==null){
			chain.doFilter(sreq, sres);
			return;
		}
		
		//abort execution, send a redirect
		log.info("Redirecting "+url+" to "+redirectUrl);
		HttpServletResponse res = (HttpServletResponse)sres;
		res.setHeader("Location", redirectUrl);
		res.sendError(301, "Moved permanently");
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		try {
			siteDataService = MetaFactory.get(IASSiteDataService.class);
		} catch (MetaFactoryException e) {
			log.error(MarkerFactory.getMarker("FATAL"), "IASSiteDataService init failure", e);
			throw new ServletException("IASSiteDataService init failure",e);
		}
	}

    /**
     * Search url in CMS. If  founded -  then url for redirect will be returned.
     * Curently supports urls  like <a>http://localhost:[port]/  && http://localhost:[port] </a>.
     * @param lookupUrl  actually url for search.
     * @return Founded url for redirect or null
     */
	private String resolveRedirectUrl(String lookupUrl){
		try{
			List<RedirectUrl> urls = siteDataService.getRedirectUrls();
			for (RedirectUrl u : urls){
				if (u.getIn()!=null && (u.getIn().equals(lookupUrl) || lookupUrl.equals(u.getIn()+ CHAR)))
					return u.getOut();
			}
		}catch(ASSiteDataServiceException e){
			log.error("resolveRedirectUrl("+lookupUrl+")", e);
		}
		return null;
	}
}
 