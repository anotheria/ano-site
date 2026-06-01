package net.anotheria.anosite.cms.action.filehandling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.anotheria.asg.util.filestorage.TemporaryFileHolder;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

/**
 * @author another
 */
public class ShowTmpFileAction extends BaseFileHandlingAction {

	public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
		TemporaryFileHolder h = getTemporaryFile(req);
		byte[] data = h.getData();
		res.getOutputStream().write(data);
		res.getOutputStream().flush();
		return null;
	}
}
