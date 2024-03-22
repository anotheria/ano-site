package net.anotheria.anosite.content.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.anosite.gen.shared.service.AnoDocConfigurator;

/**
 * The sole purpose of this filter is to be the first filter in the mapping and there to set the character encoding properly before a third party filter accessen a request parameter with a wrong
 * (or none) encoding set, and screws the attributes.
 * @author lrosenberg
 *
 */
public class FirstFilter implements Filter{ 
	@Override public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {
		if (!(sreq instanceof HttpServletRequest))
			return;
		
		HttpServletRequest req = (HttpServletRequest)sreq;
		//force to tomcat to read the attributes properly.
		req.setCharacterEncoding(AnoDocConfigurator.getEncoding());
		req.getParameter("dummy");
		
		chain.doFilter(sreq, sres);
	}

	@Override public void destroy() {
	}

	
	@Override public void init(FilterConfig arg0) throws ServletException {
	}
}
