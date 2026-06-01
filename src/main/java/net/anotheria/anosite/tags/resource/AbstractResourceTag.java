package net.anotheria.anosite.tags.resource;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.content.servlet.resource.type.ResourceReadType;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.tags.BaseTagSupport;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.jsp.JspException;

/**
 * Abstract resource TAG.
 *
 * @author h3ll
 */
public abstract class AbstractResourceTag extends BaseTagSupport {

	/**
	 * Resource data service.
	 */
	private static IASResourceDataService service;
	/**
	 * log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceTag.class);
	/**
	 * Separator.
	 */
	private static final String SEPARATOR = "/";

	/**
	 * Init.
	 */
	static {
		try {
			service = MetaFactory.get(IASResourceDataService.class);
		} catch (MetaFactoryException e) {
			LOG.error(MarkerFactory.getMarker("FATAL"), "IASResourceDataService init failure", e);
		}
	}

	/**
	 * Returns ResourceService.
	 *
	 * @return {@link IASResourceDataService}
	 */
	protected static IASResourceDataService getResourceDataService() {
		return service;
	}

	/**
	 * AbstractResourceTag property.
	 * Property name by which CMS document  should be found!
	 */
	private String selectPropertyName = ReadPropertyType.DEFAULT.getValue();
	/**
	 * AbstractResourceTag value.
	 */
	private String propertyValue;
	/**
	 * AbstractResourceTag addContextPath.
	 */
	private boolean addContextPath = true;
	/**
	 * AbstractResourceTag result.
	 * Property name -- of CMS document  which should be returned.
	 */
	private String resultPropertyName = ResultPropertyType.DEFAULT.getValue();


	/**
	 * Returns Logger instance.
	 *
	 * @return {@link Logger}
	 */
	protected static Logger getLog() {
		return LOG;
	}


	/**
	 * Returns {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ReadPropertyType} based on current selectPropertyName value.
	 *
	 * @return {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ReadPropertyType}
	 */
	protected ReadPropertyType getSelectType() {
		if (StringUtils.isEmpty(selectPropertyName))
			return ReadPropertyType.DEFAULT;

		return ReadPropertyType.getPropertyNameByValue(selectPropertyName);
	}


	public void setSelectPropertyName(String aSelectPropertyName) {
		this.selectPropertyName = aSelectPropertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public boolean isAddContextPath() {
		return addContextPath;
	}

	public void setAddContextPath(boolean addContextPath) {
		this.addContextPath = addContextPath;
	}

	/**
	 * Returns {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ResultPropertyType} based on current resultPropertyName value.
	 *
	 * @return {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ResultPropertyType}
	 */
	protected ResultPropertyType getResultType() {
		if (StringUtils.isEmpty(resultPropertyName))
			return ResultPropertyType.DEFAULT;

		return ResultPropertyType.getPropertyNameByValue(resultPropertyName);
	}


	public void setResultPropertyName(String aResultPropertyName) {
		this.resultPropertyName = aResultPropertyName;
	}

	public String getSelectPropertyName() {
		return selectPropertyName;
	}

	public String getResultPropertyName() {
		return resultPropertyName;
	}

	@Override
	public int doEndTag() throws JspException {
		if (StringUtils.isEmpty(getPropertyValue()))
			throw new JspException("required attribute - propertyValue is missing");

		//Direct file name  case!
		if (getSelectType() == ReadPropertyType.DIRECT_FILE_NAME) {
			// note  in current case - only File path will be returned! Nothing more!
			write(getResourcePath() + propertyValue);
			return SKIP_BODY;
		}

		write(getOutputData());
		return SKIP_BODY;
	}

	/**
	 * Build result itself.
	 *
	 * @return string result
	 * @throws JspException on errors
	 */
	private String getOutputData() throws JspException {
		switch (getResultType()) {
			case DOCUMENT_TITLE:
				return getTitleFromDocument(getDocument());

			case DOCUMENT_ALIAS:
				return getAliasFromDocument(getDocument());

			case DOCUMENT_ALT:
				return getAltFromDocument(getDocument());

			case FILE_OR_IMAGE:
				StringBuilder buf = new StringBuilder();
				buf.append(getResourcePath()).append(getSelectType().getValue()).append(SEPARATOR).append(getPropertyValue());
				return buf.toString();
			default:
				String message = getResultPropertyName() + " not supported for " + this.getClass().getName() + "! Refer to implementation Please!";
				LOG.warn(message);
				throw new RuntimeException(message);
		}

	}

	/**
	 * Returns document title - if available.
	 *
	 * @param document {@link DataObject}
	 * @return string property
	 */
	protected abstract String getTitleFromDocument(DataObject document);

	/**
	 * Returns alias title - if available.
	 *
	 * @param document {@link DataObject}
	 * @return string property
	 */
	protected abstract String getAliasFromDocument(DataObject document);

	/**
	 * Returns alt attribute value - if available.
	 *
	 * @param document
	 * 		{@link DataObject} instance
	 * @return
	 * 		{@code String} property
	 */
	protected abstract String getAltFromDocument(DataObject document);


	/**
	 * Return resource path.
	 *
	 * @return resource path
	 */
	protected abstract String getResourcePath();


	/**
	 * Return {@link DataObject} according to incoming {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ReadPropertyType}.
	 *
	 * @return {@link DataObject}
	 * @throws JspException on errors
	 */
	protected abstract DataObject getDocument() throws JspException;

	/**
	 * Defines result  type - which should be returned  by tag.
	 * Available result properties  enumeration.
	 */
	protected enum ResultPropertyType {
		/**
		 * Title of selected document.
		 */
		DOCUMENT_TITLE("title"),
		/**
		 * Alias of  document! Can be applied  to CMS_Image  only!
		 */
		DOCUMENT_ALIAS("alias"),
		/**
		 * Alt attribute of document. Can be applied  to CMS_Image  only!
		 */
		DOCUMENT_ALT("alt"),
		/**
		 * File or  image property. (Depends on document instance).
		 */
		FILE_OR_IMAGE("file_or_image");

		/**
		 * Default constant.
		 */
		protected static final ResultPropertyType DEFAULT = FILE_OR_IMAGE;


		/**
		 * ResultPropertyType
		 */
		private String value;

		/**
		 * Constructor.
		 *
		 * @param aValue value
		 */
		ResultPropertyType(String aValue) {
			this.value = aValue;
		}


		public String getValue() {
			return value;
		}

		/**
		 * Return {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ResultPropertyType} with selected value.
		 *
		 * @param val value
		 * @return {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ResultPropertyType}
		 */
		public static ResultPropertyType getPropertyNameByValue(final String val) {
			for (ResultPropertyType documentSelectProperty : ResultPropertyType.values())
				if (documentSelectProperty.getValue().equals(val))
					return documentSelectProperty;

			LoggerFactory.getLogger(ResultPropertyType.class).error("No ResultPropertyType found with value[" + val + "], relying on defaults!");
			return DEFAULT;
		}

	}

	/**
	 * Defines property type - by which tag  should read  document, or build  result.
	 * Available select Property names enumeration.
	 */
	protected enum ReadPropertyType {
		/**
		 * Document id.
		 */
		DOCUMENT_ID(ResourceReadType.BY_ID.getValue()),
		/**
		 * Document name.
		 */
		DOCUMENT_NAME(ResourceReadType.BY_NAME.getValue()),
		/**
		 * File name.
		 */
		DIRECT_FILE_NAME(ResourceReadType.BY_DIRECT_FILE_NAME.getValue());
		/**
		 * Default constant.
		 */
		protected static final ReadPropertyType DEFAULT = DOCUMENT_NAME;

		/**
		 * ReadPropertyType value.
		 */
		private String value;

		/**
		 * Constructor.
		 *
		 * @param aValue current value
		 */
		ReadPropertyType(String aValue) {
			this.value = aValue;
		}

		public String getValue() {
			return value;
		}

		/**
		 * Return {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ReadPropertyType} with selected value.
		 *
		 * @param val value
		 * @return {@link net.anotheria.anosite.tags.resource.AbstractResourceTag.ReadPropertyType}
		 */
		public static ReadPropertyType getPropertyNameByValue(final String val) {
			for (ReadPropertyType documentSelectProperty : ReadPropertyType.values())
				if (documentSelectProperty.getValue().equals(val))
					return documentSelectProperty;

			LoggerFactory.getLogger(ReadPropertyType.class).error("No ReadPropertyType found with value[" + val + "], relying on defaults!");
			return DEFAULT;
		}
	}


}
