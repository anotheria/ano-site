package net.anotheria.anosite.content.servlet.resource.type;


import org.slf4j.LoggerFactory;

/**
 * Defines ways/properties for resource read - by Resource servlet.
 *
 * @author h3ll
 */
public enum ResourceReadType {
	/**
	 * By document id.
	 */
	BY_ID("id"),
	/**
	 * By document name.
	 */
	BY_NAME("name"),
	/**
	 * Directly from FS by fileName.
	 */
	BY_DIRECT_FILE_NAME("fileName");


	private String value;

	/**
	 * Constructor.
	 *
	 * @param aValue string representation
	 */
	ResourceReadType(String aValue) {
		this.value = aValue;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Return ResourceReadType by value! If  no such ResourceReadType found, default will be returned.
	 *
	 * @param value string value
	 * @return {@link ResourceReadType}
	 */
	public static ResourceReadType getByValue(final String value) {
		for (ResourceReadType type : ResourceReadType.values())
			if (type.getValue().equals(value))
				return type;
		LoggerFactory.getLogger(ResourceReadType.class).error("No ResourceReadType found with value " + value + " Returning defaults.");
		return BY_ID;

	}
}
