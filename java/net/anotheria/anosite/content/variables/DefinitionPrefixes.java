package net.anotheria.anosite.content.variables;

import net.anotheria.util.content.template.processors.variables.ConditionPrefixes;

/**
 * This helper class defines prefixes useable for variables in the variable processor.
 *
 * @author lrosenberg
 */
public final class DefinitionPrefixes {
	/**
	 * Http parameter.
	 */
	public static final String PREFIX_PARAMETER = "p";
	/**
	 * Http request attribute.
	 */
	public static final String PREFIX_REQUEST_ATTRIBUTE = "ra";
	/**
	 * Box translations.
	 */
	public static final String PREFIX_LOCALIZATION_MESSAGE = "mes";
	/**
	 * Attribute in the internal scope of the api call context.
	 */
	public static final String PREFIX_API_CALL_CONTEXT_ATTRIBUTE = "api_cca";
	/**
	 * (Http) Session attribute.
	 */
	public static final String PREFIX_SESSION_ATTRIBUTE = "sa";
	/**
	 * (Http) Session attribute which is one time readable.
	 */
	public static final String PREFIX_SESSION_AND_DELETE_ATTRIBUTE = "sda";
	/**
	 * Servlet context attribute.
	 */
	public static final String PREFIX_CONTEXT_ATTRIBUTE = "ca";
	/**
	 * Api session attribute.
	 */
	public static final String PREFIX_API_SESSION_ATTRIBUTE = "api_sa";
	/**
	 * Box attribute.
	 */
	public static final String PREFIX_BOX_ATTRIBUTE = "ba";
	/**
	 * Page attribute.
	 */
	public static final String PREFIX_PAGE_ATTRIBUTE = "pa";
	/**
	 * Brand attribute.
	 */
	public static final String PREFIX_BRAND_ATTRIBUTE = "b_a";
	/**
	 * Constant, performed by constants processor.
	 */
	public static final String PREFIX_CONSTANT = "c";
	/**
	 * Constant, performed by calendar processor.
	 */
	public static final String PREFIX_CALENDAR = "cal";
	/**
	 * Link to an image.
	 */
	public static final String PREFIX_IMAGE_LINK = "imagelink";
	/**
	 * Link to a file.
	 */
	public static final String PREFIX_FILE_LINK = "filelink";
	/**
	 * Image (including tags).
	 */
	public static final String PREFIX_IMAGE = "image";
	/**
	 * Prefix If.
	 */
	public static final String PREFIX_IF = ConditionPrefixes.PREFIX_IF;
	/**
	 * Prefix if Not.
	 */
	public static final String PREFIX_IF_NOT = ConditionPrefixes.PREFIX_IF_NOT;
	/**
	 * Prefix Present.
	 */
	public static final String PREFIX_PRESENT = ConditionPrefixes.PREFIX_PRESENT;
	/**
	 * Prefix not Present.
	 */
	public static final String PREFIX_NOT_PRESENT = ConditionPrefixes.PREFIX_NOT_PRESENT;
	/**
	 * Prefix equals.
	 */
	public static final String PREFIX_EQUALS = ConditionPrefixes.PREFIX_EQUALS;
	/**
	 * Prefix not equals.
	 */
	public static final String PREFIX_NOT_EQUALS = ConditionPrefixes.PREFIX_NOT_EQUALS;
	/**
	 * Prefix in range.
	 */
	public static final String PREFIX_IN_RANGE = ConditionPrefixes.PREFIX_IN_RANGE;
	/**
	 * Prefix Greater then.
	 */
	public static final String PREFIX_GREATERTHEN = ConditionPrefixes.PREFIX_GREATERTHEN;
	/**
	 * Prefix greater equal.
	 */
	public static final String PREFIX_GREATEREQUAL = ConditionPrefixes.PREFIX_GREATEREQUAL;
	/**
	 * Prefix Less Then.
	 */
	public static final String PREFIX_LESSTHAN = ConditionPrefixes.PREFIX_LESSTHAN;
	/**
	 * Prefix Less Equal.
	 */
	public static final String PREFIX_LESSEQUAL = ConditionPrefixes.PREFIX_LESSEQUAL;

	private DefinitionPrefixes() {
		//prevent instantiation.
	}
}
