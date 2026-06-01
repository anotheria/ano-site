package net.anotheria.anosite.cms.helper;

import net.anotheria.anosite.gen.aswebdata.data.Box;

/**
 * BoxSubHelper interface.
 */
public interface BoxSubHelper {
	/**
	 * Returns field explanation.
	 * @param box box
	 * @param property property
	 * @return field explanation as string
	 */
	public String getFieldExplanation(Box box, String property);
}
