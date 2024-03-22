package net.anotheria.anosite.content.servlet;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.aslayoutdata.data.PageStyle;
import net.anotheria.anosite.gen.aslayoutdata.service.ASLayoutDataServiceException;
import net.anotheria.anosite.gen.aslayoutdata.service.IASLayoutDataService;
import net.anotheria.moskito.web.MoskitoHttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
/**
 * This servlet is responsible for delivering the styles to the users browser.
 * @author lrosenberg
 */
public class StyleSheetServlet extends MoskitoHttpServlet {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger fatalLogger = LoggerFactory.getLogger(StyleSheetServlet.class);

	/**
	 * default serial version uid.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Layout data service for retrieval of stylesheets.
	 */
	private IASLayoutDataService layoutDataService;

	@Override public void init(ServletConfig config) throws ServletException{
		super.init(config);
		try {
			layoutDataService = MetaFactory.get(IASLayoutDataService.class);
		} catch (MetaFactoryException e) {
			fatalLogger.error(MarkerFactory.getMarker("FATAL"), "ASG service init failure", e);
			throw new ServletException("ASG services init failure",e);
		}
	}

	@Override
	protected void moskitoDoGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		if (path.length()>0 && path.charAt(0)=='/')
			path = path.substring(1);
		int indexOfDot = path.indexOf('.');
		if (indexOfDot!=-1)
			path = path.substring(0, indexOfDot);
		
		try{
			PageStyle style = layoutDataService.getPageStyle(path);
			OutputStream out = null;
			try{
				res.setContentType("text/css");
				out = res.getOutputStream();
				out.write(style.getCss().getBytes());
				out.flush();
				out.close();
				out = null;
			}finally{
				if (out!=null){
					try{
						out.close();
					}catch(IOException ignored){}
				}
			}
		}catch(ASLayoutDataServiceException e){
			throw new ServletException(e);
		}
	}
}
