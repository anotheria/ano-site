package net.anotheria.anosite.tags.util;

import jakarta.servlet.jsp.JspException;

import net.anotheria.tags.BaseTagSupport;
import net.anotheria.anosite.util.AnositeConstants;

/**
 * Writes out the content of the resource. Writes out a link to the resource in edit mode.
 * @author another
 *
 */
public class RandomTag extends BaseTagSupport{


	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6768997353263852167L;

	@Override public int doEndTag() throws JspException {

		Object asRandom = pageContext.getServletContext().getAttribute(AnositeConstants.AA_ANOSITE_RANDOM);
		if(asRandom != null)
			write("random=" + asRandom.toString());
		return SKIP_BODY;
	}

}
