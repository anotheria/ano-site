package net.anotheria.anosite.content.filter;

import static net.anotheria.anosite.util.AnositeConstants.PARAM_SWITCH_MODE;
import static net.anotheria.anosite.util.AnositeConstants.PARAM_VALUE_EDIT_MODE;
import static net.anotheria.anosite.util.AnositeConstants.PARAM_VALUE_VIEW_MODE;
import static net.anotheria.anosite.util.AnositeConstants.SA_EDIT_MODE_FLAG;

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
 * This filter is used to switch the on-site edit mode on and off. To perform this task it checks whether the switch mode parameter is present.
 * @author another
 *
 */
public class EditModeSwitcherFilter implements Filter{

	@Override public void destroy() {
		
	}

	@Override public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {
		if (!(sreq instanceof HttpServletRequest))
			return;
		HttpServletRequest req = (HttpServletRequest)sreq;
		req.setCharacterEncoding(AnoDocConfigurator.getEncoding());
		
		String p = req.getParameter(PARAM_SWITCH_MODE);
		if (p!=null && p.length()>0){
			if (p.equals(PARAM_VALUE_EDIT_MODE))
				req.getSession().setAttribute(SA_EDIT_MODE_FLAG, Boolean.TRUE);
			if (p.equals(PARAM_VALUE_VIEW_MODE))
				req.getSession().removeAttribute(SA_EDIT_MODE_FLAG);
		}
		
		chain.doFilter(sreq, sres);
	}

	@Override public void init(FilterConfig arg0) throws ServletException {
		
	}

}
