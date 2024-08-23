package net.anotheria.anosite.shared.presentation.filter;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.assiteconfig.data.FilteredIP;
import net.anotheria.anosite.gen.assiteconfig.service.IASSiteConfigService;
import net.anotheria.anosite.gen.shared.data.AllowanceUtils;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.util.StringUtils;
import net.anotheria.util.network.IPRange;
import net.anotheria.util.network.PlainIPFilter;
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
import java.util.ArrayList;
import java.util.List;


/**
 * The filter which performs request's IP through allowance/restriction IP filtering rules 
 * that are loaded from the CMS (<i>see CSM SiteConfig.FilteredIP</i>).
 * In case when an IP at the same time is allowed and restricted allowance has bigger priority. 
 * For restricted IPs is sent response with redirection on maintenance that configured via "maintenancePage"
 * filter parameter in <i>web.xml</i> 
 * If "maintenancePage" parameter is not set then "403" response is returned.
 *
 * By default every application resource is in the restricted zone (zone for which IP filtering is performing) 
 * that can be changed by describing restricted resources by paths and/or by extensions.
 * To describe restricted zone are used two initial filter parameters in web.xml:
 * <ol>
 * <li>restrictedPaths with value as comma separated list of paths without leading slash symbol.
 * 	E.g "cms,mui,admintools" restricts access to http://foo.bar/cms/index, http://foo.bar/mui/showAllProducers and so on</li>
 * <li>restrictedExtensions with value as comma separated list of extensions without leading dot symbol.
 * 	E.g. "html,action,js" restricts access to http://foo.bar/home.html, http://foo.bar/login.action and so on</li>
 * </ol>
 * Everything else doesn't target to restricted zone.
 * 
 * @author dmetelin
 */
public class IPFilter implements Filter{

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(IPFilter.class);

	private static enum Allowance{ALLOWED,RESTRICTED};
	

	private IASSiteConfigService siteConfigService;

	private String maintenancePage;
	private String[] restrictedExtensions;
	private String[] restrictedPaths;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		maintenancePage = config.getInitParameter("maintenancePage");
		
		String paramExt =  config.getInitParameter("restrictedExtensions");
		restrictedExtensions = !StringUtils.isEmpty(paramExt)? StringUtils.tokenize(paramExt,','): new String[0];
		
		String paramPaths =  config.getInitParameter("restrictedPaths");
		restrictedPaths = !StringUtils.isEmpty(paramPaths)? StringUtils.tokenize(paramPaths,','): new String[0];
		
		try {
			siteConfigService = MetaFactory.get(IASSiteConfigService.class);
		} catch (MetaFactoryException e) {
			log.error(MarkerFactory.getMarker("FATAL"), "Could not initialize IASSiteConfigService: ", e);
			throw new ServletException("Could not initialize IASSiteConfigService: " + e.getMessage());
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {
		if (!(sreq instanceof HttpServletRequest))
			return; 
		
		HttpServletRequest req = (HttpServletRequest) sreq;
		
		if(!isRestrictedZoneReq(req)){
			log.debug("Pass to not restricted zone: " + req.getRequestURL());
			chain.doFilter(sreq, sres);
			return;
		}
		
		try{
			String filterableIP = getFilterableIP(req);
			List<FilteredIP> filteredIPs = getFilteredIPs();
			log.debug("IP: " + filterableIP + ", FilteredIPs: " + filteredIPs);
			if(PlainIPFilter.mayPass(filterableIP,getIPRanges(filteredIPs, Allowance.ALLOWED))){
				log.debug("IP is Allowed: Go to the next Filter in the chain");
				chain.doFilter(sreq, sres);
				return;
			}
			boolean mayNotPass = false;
			try{
				mayNotPass = PlainIPFilter.mayPass(filterableIP,getIPRanges(filteredIPs, Allowance.RESTRICTED)); 
			}catch(RuntimeException e){
				log.warn("Exception parsing the ip, will not restrict.", e);
			}
			if(mayNotPass){
				log.debug("IP is restricted: redirect to maintenance page");
				HttpServletResponse res = (HttpServletResponse)sres;
				String url = getMaintenancePageURL(req);
				if(url != null)
					res.sendRedirect(url);
				res.setStatus(403);
				return;
			}
		}catch(ASGRuntimeException e){
			log.error("doFilter", e);
			throw new ServletException("ASG Runtime Exception: " + e.getMessage());
		}
		
		log.debug("Current IP is not Filtered: just continue normal flow.");
		chain.doFilter(sreq, sres);
	}
	
	private String getFilterableIP(HttpServletRequest req){
		return req.getRemoteAddr();
	}

	private List<FilteredIP> getFilteredIPs() throws ASGRuntimeException{
		return siteConfigService.getFilteredIPs();
	}
	
	
	private List<IPRange> getIPRanges(List<FilteredIP> filteredIPs, Allowance allowance){
		int allowanceValue = allowance == Allowance.ALLOWED? AllowanceUtils.Allowed: AllowanceUtils.Restricted;
		List<IPRange> ret = new ArrayList<IPRange>();
		for(FilteredIP ip:filteredIPs)
			if(allowanceValue == ip.getAllowance()){
				try{
					ret.add(toIPRagne(ip));
				}catch(Exception e){
					log.error("Could not parse FilteredIP: ", e);
				}
			}
		return ret;
			
	}
	
	private IPRange toIPRagne(FilteredIP filteredIP){
		return new IPRange(filteredIP.getIpAddress(), filteredIP.getMask() != 0? filteredIP.getMask():32);
	}
	
	/**
	 * @param req TODO dummy comment for javadoc.
	 * @return URL to maintenance page
	 */
	protected String getMaintenancePageURL(HttpServletRequest req){
		if(StringUtils.isEmpty(maintenancePage))
			return null;
		if(maintenancePage.startsWith("http://") || maintenancePage.startsWith("https://"))
			return maintenancePage;
		
		return req.getContextPath()+"/"+maintenancePage;
	}
	
	/**
	 * Checks whether or not request came to restricted zone
	 * @param req request
	 * @return true if request to restricted zone
	 */
	protected boolean isRestrictedZoneReq(HttpServletRequest req){
		//Maintenance page is always allowed
		if(req.getRequestURI().equals(getMaintenancePageURL(req)))
			return false;
		
		String resourceURI = req.getRequestURI().substring(req.getContextPath().length() + 1);
		String path = StringUtils.getStringBefore(resourceURI, "/");
		String ext = StringUtils.getStringAfter(resourceURI, ".");

		//If no explicit restrictions then everything is restricted
		if(restrictedExtensions.length == 0 && restrictedPaths.length == 0)
			return true;
		
		
		for(String rPath:restrictedPaths)
			if(path.equals(rPath))
				return true;
		
		for(String rExt:restrictedExtensions)
			if(ext.equals(rExt))
				return true;
		
		return false;
	}
	
	
	
}
