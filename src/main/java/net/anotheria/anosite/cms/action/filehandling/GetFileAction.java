package net.anotheria.anosite.cms.action.filehandling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.anotheria.asg.util.filestorage.FileStorage;
import net.anotheria.asg.util.filestorage.TemporaryFileHolder;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

/**
 * @author lrosenberg
 */
public class GetFileAction extends BaseFileHandlingAction {

	public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {

		String name = getStringParameter(req, "pName");
		if (name.startsWith("../"))
			throw new RuntimeException("Not allowed!");

		TemporaryFileHolder h = FileStorage.loadFile(name);
		if(h == null){
			log.warn("Could not load file with name '" + name + "'!");
			return null;
		}

		byte[] data = h.getData();
		String mimeType = h.getMimeType(); 
		if (mimeType!=null)
			res.setContentType(mimeType);
		if (data!=null){
			res.setContentLength(data.length);
			res.getOutputStream().write(data);
		}
		res.getOutputStream().flush();
		return null;
	}
}