package net.anotheria.anosite.tags.resource.image;

import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.shared.ResourceServletMappingConfig;
import net.anotheria.anosite.tags.resource.AbstractResourceTag;
import net.anotheria.asg.data.DataObject;

import jakarta.servlet.jsp.JspException;
import java.util.List;

/**
 * Anotheria Image Tag.
 *
 * @author h3ll
 */
public class CmsImageTag extends AbstractResourceTag {

	@Override
	protected String getTitleFromDocument(DataObject document) {
		if (!(document instanceof Image)) {
			return "Image " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing";
		}
		return Image.class.cast(document).getTitle();
	}

	@Override
	protected String getAliasFromDocument(DataObject document) {
		if (!(document instanceof Image)) {
			return "Image " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing";
		}
		return Image.class.cast(document).getAlias();
	}

	@Override
	protected String getAltFromDocument(DataObject document) {
		if(!(document instanceof Image)){
			return "Image " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing";
		}
		String altValue = Image.class.cast(document).getAlt();
		if(altValue.isEmpty()){
			return Image.class.cast(document).getTitle();
		}
		else {
			return altValue;
		}
	}

	@Override
	protected String getResourcePath() {
		String contextPath = pageContext.getServletContext().getContextPath();
		String servletMapping = ResourceServletMappingConfig.getInstance().getImageServletMapping();
		if (!contextPath.isEmpty() && isAddContextPath())
			return contextPath + servletMapping;
		return servletMapping;
	}


	@Override
	protected DataObject getDocument() throws JspException {
		Image document = null;
		switch (getSelectType()) {
			case DOCUMENT_ID:
				try {
					document = getResourceDataService().getImage(getPropertyValue());
					if (document == null)
						getLog().warn("Image " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing");
					return document;
				} catch (NoSuchDocumentException nSde) {
					getLog().warn("Image " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing");
					return null;
				} catch (ASResourceDataServiceException e) {
					getLog().warn("ASResourceDataService failed", e);
					throw new JspException("Internal server error", e);
				}

			case DOCUMENT_NAME:
				try {

					List<Image> images = getResourceDataService().getImagesByProperty(Image.PROP_NAME, getPropertyValue());
					if (images != null && !images.isEmpty())
						document = images.get(0);
					if (document == null)
						getLog().warn("Image " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing");
					return document;
				} catch (ASResourceDataServiceException e) {
					getLog().warn("ASResourceDataService failed", e);
					throw new JspException("Internal server error", e);
				}
			default:
				throw new RuntimeException(getSelectPropertyName() + " not supported in CmsImageTag! Refer to implementation Please!");

		}
	}


}
