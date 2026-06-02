package net.anotheria.anosite.content.variables;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.FileLink;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;

import net.anotheria.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.List;

/**
 * Processor for links to files (for example PDF) which are uploaded via IASResourceDataService.
 *
 * @author lrosenberg
 */
public class FileLinkProcessor extends XLinkProcessor {
	/**
	 * Service to lookup files.
	 */
	private static IASResourceDataService service;

	/**
	 * Static init block.
	 */
	static {
		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(ImageLinkProcessor.class).error(MarkerFactory.getMarker("FATAL"), "Not properly initialized, can't find resource data service.");
		}

	}


	@Override
	protected String getResourcePath(String contextPath) {
		String result = getResourceServletMappingConfiguration().getFileServletMapping();
		if (!StringUtils.isEmpty(contextPath))
			result = contextPath + result;
		return result;
	}

	@Override
	protected String getFileName(String variable) {
		try {
			List<FileLink> files = service.getFileLinksByProperty(FileLink.PROP_NAME, variable);
			if (files == null || files.isEmpty()) {
				getLog().debug("File with name [" + variable + "] not found");
				return null;
			}
			FileLink file = files.get(0);
			if (file == null) {
				getLog().debug("File with name [" + variable + "] found! But  due to some strange reasons is NULL");
				return null;
			}

			if (!StringUtils.isEmpty(file.getFile()))
				return file.getFile();

			throw new RuntimeException("CMS resource FILE name[" + variable + "] property file is miss-configured! Check Resource please!" + file);
		} catch (ASResourceDataServiceException e) {
			getLog().error("getFileName(" + variable + ") failure: ", e);
			return null;
		}
	}
}
