package net.anotheria.anosite.cms.action.filehandling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.anotheria.asg.util.filestorage.TemporaryFileHolder;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

/**
 * @author another
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ShowTmpFileAction extends BaseFileHandlingAction {

	/* (non-Javadoc)
	 * @see net.anotheria.webutils.actions.BaseAction#doExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
		TemporaryFileHolder h = getTemporaryFile(req);
		byte[] data = h.getData();
		res.getOutputStream().write(data);
		res.getOutputStream().flush();
		return null;
	}
}
