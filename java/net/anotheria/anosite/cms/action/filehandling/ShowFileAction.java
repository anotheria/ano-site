package net.anotheria.anosite.cms.action.filehandling;

import net.anotheria.anosite.cms.bean.UploadFileBean;
import net.anotheria.asg.util.filestorage.IFilesConstants;
import net.anotheria.asg.util.filestorage.TemporaryFileHolder;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author another
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ShowFileAction extends BaseFileHandlingAction {

	public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception{
		UploadFileBean filebean = new UploadFileBean();
		TemporaryFileHolder holder = getTemporaryFile(req);

		if (holder==null){
			filebean.setName("Noch kein File.");
			filebean.setSize(makeSizeString(0));
			filebean.setMessage("Warte aufs File.");
			filebean.setFilePresent(false);
		}else{
			filebean.setName(holder.getFileName());
			filebean.setSize(makeSizeString(holder.getSize()));
			filebean.setMessage("File hochgeladen");
			filebean.setFilePresent(true);
		}

		addBeanToSession(req, IFilesConstants.BEAN_FILE, filebean);
		return mapping.success();
	}
}
