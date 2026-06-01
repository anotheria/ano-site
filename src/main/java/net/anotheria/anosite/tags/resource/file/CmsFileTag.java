package net.anotheria.anosite.tags.resource.file;

import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anosite.gen.asresourcedata.data.FileLink;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.shared.ResourceServletMappingConfig;
import net.anotheria.anosite.tags.resource.AbstractResourceTag;
import net.anotheria.asg.data.DataObject;

import jakarta.servlet.jsp.JspException;
import java.util.List;

/**
 * Anotheria CMS-File Tag.
 *
 * @author h3ll
 */
public class CmsFileTag extends AbstractResourceTag {


	@Override
	protected String getTitleFromDocument(DataObject document) {
		if (!(document instanceof FileLink)) {
			return "FileLink " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing";
		}
		return FileLink.class.cast(document).getTitle();
	}

	@Override
	protected String getAliasFromDocument(DataObject document) {
		throw new RuntimeException(getResultPropertyName() + " not supported for CmsFileTag! Refer to implementation Please!");
	}

	@Override
	protected String getResourcePath() {
		String contextPath = pageContext.getServletContext().getContextPath();
		String servletMapping = ResourceServletMappingConfig.getInstance().getFileServletMapping();
		if (!contextPath.isEmpty() && isAddContextPath())
			return contextPath + servletMapping;
		return servletMapping;
	}

	@Override
	protected String getAltFromDocument(DataObject document) {
		throw new RuntimeException(getResultPropertyName() + " not supported for CmsFileTag! Refer to implementation Please!");
	}

	@Override
	protected DataObject getDocument() throws JspException {
		FileLink document = null;

		switch (getSelectType()) {
			case DOCUMENT_ID:

				try {
					document = getResourceDataService().getFileLink(getPropertyValue());
					if (document == null)
						getLog().warn("FileLink " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing");
					return document;
				} catch (NoSuchDocumentException nSde) {
					getLog().warn("FileLink " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing");
					return null;

				} catch (ASResourceDataServiceException e) {
					getLog().warn("ASResourceDataService failed", e);
					throw new JspException("Internal server error", e);
				}
			case DOCUMENT_NAME:
				try {
					List<FileLink> files = getResourceDataService().getFileLinksByProperty(FileLink.PROP_NAME, getPropertyValue());
					if (files != null && !files.isEmpty())
						document = files.get(0);
					if (document == null)
						getLog().warn("FileLink " + getSelectType().getValue() + "[" + getPropertyValue() + "] ::: missing");
					return document;

				} catch (ASResourceDataServiceException e) {
					getLog().warn("ASResourceDataService failed", e);
					throw new JspException("Internal server error", e);
				}
			default:
				throw new RuntimeException(getSelectPropertyName() + " not supported in CmsFileTag! Refer to implementation Please!");
		}

	}


}
