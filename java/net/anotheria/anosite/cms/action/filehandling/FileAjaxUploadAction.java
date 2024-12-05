package net.anotheria.anosite.cms.action.filehandling;

import net.anotheria.anosite.cms.bean.UploadFileBean;
import net.anotheria.asg.util.filestorage.FileStorage;
import net.anotheria.asg.util.filestorage.IFilesConstants;
import net.anotheria.asg.util.filestorage.TemporaryFileHolder;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.util.IOUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Uploads a file and saves it to the session temporary.
 */
public class FileAjaxUploadAction extends BaseFileHandlingAction {

	public ActionCommand execute(ActionMapping mapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
		UploadFileBean fileBean = new UploadFileBean ();
		upload(req, res, fileBean);
		addBeanToSession(req, IFilesConstants.BEAN_FILE, fileBean);
		return mapping.success();
	}

	/**
	 * Uploads the file.
	 * @param req
	 * @param res
	 * @param fileBean
	 * @return
	 * @throws IOException
	 */	
	private void upload(HttpServletRequest req, HttpServletResponse res, UploadFileBean fileBean) throws IOException{
		log.debug("trying uploading file....");
		
        PrintWriter writer = null;
        InputStream is = null;

        try {
            writer = res.getWriter();
        } catch (IOException ex) {
        	log.error("Error while file upploading: ",ex);
			throw new RuntimeException("Error while file upploading. Check logs for details!");
        }
				
		try{	
			log.debug("have the request...");
			String name = req.getHeader("X-File-Name");
			is = req.getInputStream();			
			if(is==null)
				throw new RuntimeException("Uploaded empty file!");

			byte[] data = IOUtils.readBytes(is);
			String fileName = FileStorage.generateFileName(name);

			TemporaryFileHolder fileHolder = new TemporaryFileHolder();
			fileHolder.setData(data);
			fileHolder.setFileName(fileName);
			
			//now store it
			String propertyName = req.getParameter("property");
			if(propertyName == null)
				storeTemporaryFile(req, fileHolder);
			else
				storeTemporaryFile(req, fileHolder, propertyName);
			fileBean.setName(fileName);
			fileBean.setSize(makeSizeString(data.length));
			fileBean.setMessage("Erfolgreich gespeichert.");
			fileBean.setFilePresent(true);
			
			writer.print("{success: true}");
		} catch(Exception ex){
			log.error("Error while file upploading: ",ex);
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.print("{success: false}");
		} finally {
			IOUtils.closeIgnoringException(is);
		}
	}
}
