package net.anotheria.anosite.content.servlet.resource;

import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anosite.content.servlet.resource.type.ResourceReadType;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;

import jakarta.servlet.ServletException;
import java.util.List;

/**
 * CMSImageServlet as ResourceServlet.
 *
 * @author h3ll
 */
public class CMSImageServlet extends ResourceServlet {

	@Override
	protected String getFileName(ResourceReadType type, String propertyValue) throws ServletException {

		if (type == ResourceReadType.BY_DIRECT_FILE_NAME)
			return propertyValue;

		Image image;
		switch (type) {
			case BY_ID:
				try {
					image = getResourceService().getImage(propertyValue);
					if (image != null)
						return image.getImage();

					getLog().warn("Image " + type.getValue() + "[" + propertyValue + "] ::: missing");
					return null;
				} catch (NoSuchDocumentException nSde) {
					getLog().warn("Image " + type.getValue() + "[" + propertyValue + "] ::: missing");
					return null;
				} catch (ASResourceDataServiceException e) {
					getLog().error("ASResourceDataServiceException failed", e);
					throw new ServletException("Internal Server Error", e);
				}
			case BY_NAME:
				try {
					List<Image> images = getResourceService().getImagesByProperty(Image.PROP_NAME, propertyValue);
					if (images != null && !images.isEmpty() && images.get(0) != null)
						return images.get(0).getImage();

					getLog().warn("Image " + type.getValue() + "[" + propertyValue + "] ::: missing");
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
