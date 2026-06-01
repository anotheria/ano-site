package net.anotheria.anosite.util;

public final class AnositeConstants {
	/**
	 * Bean prefix.
	 */
	public static final String BEAN_PREFIX = "anosite.";
	/**
	 * AA - application attribute.
	 */
	public static final String AA_PREFIX = "aa.";
	/**
	 * SA - session attribute.
	 */
	public static final String SA_PREFIX = "sa.";

	/**
	 * RA - request attribute.
	 */
	public static final String RA_PREFIX = "ra.";

	/**
	 * ASA - API Session attribute.
	 */

	public static final String ASA_PREFIX = "asa.";
	/**
	 * ACA - API CallContext Attribute.
	 */
	public static final String ACA_PREFIX = "aca.";
	/**
	 * Random.
	 */
	public static final String AA_ANOSITE_RANDOM = BEAN_PREFIX + AA_PREFIX + ".random";
	/**
	 * Session attribute Language.
	 */
	public static final String SA_LANGUAGE = BEAN_PREFIX + SA_PREFIX + ".lang";
	/**
	 * Session attribute locale.
	 */
	public static final String SA_LOCALE = BEAN_PREFIX + SA_PREFIX + ".locale";
    /**
     * Session attribute page name.
     */
    public static final String SA_PAGE_NAME = BEAN_PREFIX + SA_PREFIX + ".pageName";
	/**
	 * Session attribute editMode flag.
	 */
	public static final String SA_EDIT_MODE_FLAG = BEAN_PREFIX + SA_PREFIX + ".editModeFlag";
	/**
	 * Request attribute current uri.
	 */
	public static final String RA_CURRENT_URI = BEAN_PREFIX + RA_PREFIX + ".currentUri";
	/**
	 * Session attribute brand.
	 */
	public static final String SA_BRAND = BEAN_PREFIX + SA_PREFIX + ".brand";

	/**
	 * Language parameter.
	 */
	public static final String PARAM_LANGUAGE = "lang";
	/**
	 * DE language.
	 */
	public static final String V_LANG_DE = "DE";
	/**
	 * EN language.
	 */
	public static final String V_LANG_EN = "EN";
	/**
	 * XML request flag.
	 */
	public static final String FLAG_XML_REQUEST = "xmlrequest";

	/**
	 * Moskito.
	 */
	public static final String AS_MOSKITO_SUBSYSTEM = "ano-site";

	/**
	 * Parameter for mode switching.
	 */
	public static final String PARAM_SWITCH_MODE = "pSwitchMode";
	/**
	 * Value for switching into edit mode.
	 */
	public static final String PARAM_VALUE_EDIT_MODE = "editMode";
	/**
	 * Value for switching into view mode.
	 */
	public static final String PARAM_VALUE_VIEW_MODE = "viewMode";

	private AnositeConstants() {
		throw new IllegalAccessError("Instantiation not allowed.");
	}

}
