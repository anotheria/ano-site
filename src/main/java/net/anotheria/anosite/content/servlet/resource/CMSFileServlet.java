package net.anotheria.anosite.content.servlet.resource;

import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anosite.content.servlet.resource.type.ResourceReadType;
import net.anotheria.anosite.gen.asresourcedata.data.FileLink;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;

import jakarta.servlet.ServletException;
import java.util.List;

/**
 * CMSFileServlet as ResourceServlet.
 *
 * @author h3ll
 */

public class CMSFileServlet extends ResourceServlet {

	@Override
	protected String getFileName(ResourceReadType type, String propertyValue) throws ServletException {
		if (type == ResourceReadType.BY_DIRECT_FILE_NAME)
			return propertyValue;


		FileLink file;
		switch (type) {
			case BY_ID:
				try {
					file = getResourceService().getFileLink(propertyValue);
					if (file != null)
						return file.getFile();

					getLog().warn("FileLink " + type.getValue() + "[" + propertyValue + "] ::: missing");
					return null;
				} catch (NoSuchDocumentException nSde) {
					getLog().warn("FileLink " + type.getValue() + "[" + propertyValue + "] ::: missing");
					return null;
				} catch (ASResourceDataServiceException e) {
					getLog().error("ASResourceDataServiceException failed", e);
					throw new ServletException("Internal Server Error", e);
				}
			case BY_NAME:
				try {
					List<FileLink> files = getResourceService().getFileLinksByProperty(FileLink.PROP_NAME, propertyValue);
					if (files != null && !files.isEmpty() && files.get(0) != null)
						return files.get(0).getFile();

					getLog().warn("FileLink " + type.getValue() + "[" + propertyValue + "] ::: missing");
					return null;
				} catch (ASResourceDataServiceException e) {
					getLog().error("ASResourceDataServiceException failed", e);
					throw new ServletException("Internal Server Error", e);
				}
			default:
				throw new RuntimeException(type + " NOT supported!");
		}


	}
}
