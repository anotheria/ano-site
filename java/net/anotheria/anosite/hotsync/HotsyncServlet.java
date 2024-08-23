package net.anotheria.anosite.hotsync;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.anotheria.anosite.shared.presentation.servlet.BaseAnoSiteServlet;

public class HotsyncServlet extends BaseAnoSiteServlet{
	
	private static final String OP_GET_OBJECT_INFO = "getoinfo";
	
	public static final String PARAM_ID = "id";
	public static final String PARAM_TYPE = "type";

	@Override
	protected void moskitoDoGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		super.moskitoDoGet(req, res);
		
		String op = req.getParameter("op");
		if (op==null || op.length()==0)
			return ;
		
		if (op.equals(OP_GET_OBJECT_INFO)){
			getObjectInfo(req, res);
			return;
		}
		
		res.sendError(404, "OP "+op+" not found.");
		
	}
	
	private void getObjectInfo(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
	}
	
}
