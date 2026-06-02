package net.anotheria.anosite.guard;

import net.anotheria.anosite.util.AnositeConstants;

/**
 * Only fulfilled if the cms is in (inline) edit mode.
 * @author another
 *
 */
public class InEditModeGuard extends SessionFlagPresentGuard{

	@Override
	protected String getFlagName() {
		return AnositeConstants.SA_EDIT_MODE_FLAG;
	}
}
