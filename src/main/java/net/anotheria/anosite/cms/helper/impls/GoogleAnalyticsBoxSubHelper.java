package net.anotheria.anosite.cms.helper.impls;

import net.anotheria.anosite.cms.helper.BoxSubHelper;
import net.anotheria.anosite.gen.aswebdata.data.Box;

/**
 * GoogleAnalytics BoxSubHelper as SubHelper for BOX.
 */
public class GoogleAnalyticsBoxSubHelper implements BoxSubHelper{

	@Override
	public String getFieldExplanation(Box box, String property) {

		if (property.equals(Box.PROP_PARAMETER1))
			return "Google Analytics AccountId: _uacct";
		return null;
	}

}
