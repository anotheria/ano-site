package net.anotheria.anosite.cms.helper;

import net.anotheria.anosite.cms.helper.impls.GoogleAnalyticsBoxSubHelper;
import net.anotheria.anosite.cms.helper.impls.IfSetBoxSubHelper;
import net.anotheria.asg.util.helper.cmsview.CMSViewHelperRegistry;

/**
 * BoxHelperUtility class.
 */
public class BoxHelperUtility {
	/**
	 * Setup itself.
	 */
	public static void setup(){
		BoxTypeBoxHelper typeHelper = new BoxTypeBoxHelper();
		typeHelper.addSubHelper("GoogleAnalytics", new GoogleAnalyticsBoxSubHelper());
		typeHelper.addSubHelper("IfSet", new IfSetBoxSubHelper());
		CMSViewHelperRegistry.addCMSViewHelper("ASWebData.Box", typeHelper);
	}
}
