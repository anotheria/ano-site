package net.anotheria.anosite.wizard.stepcommandhandler;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.handler.AbstractBoxHandler;
import net.anotheria.anosite.handler.BoxHandlerResponse;
import net.anotheria.anosite.handler.ResponseContinue;
import net.anotheria.anosite.handler.exception.BoxProcessException;
import net.anotheria.anosite.handler.exception.BoxSubmitException;
import net.anotheria.anosite.wizard.WizardCommand;
import net.anotheria.anosite.wizard.api.WizardAO;
import net.anotheria.anosite.wizard.api.WizardAPI;
import net.anotheria.anosite.wizard.api.WizardStepAO;
import net.anotheria.anosite.wizard.api.exception.WizardAPIException;
import net.anotheria.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Default WizardStep handler - which allow adjusting to configured step.
 *
 * @author h3ll
 */
public class DefaultStepCommandsHandler extends AbstractBoxHandler {
	/**
	 * {@link WizardAPI}
	 */
	private static WizardAPI wizardAPI;

	/**
	 * Static init.
	 */
	static {
		wizardAPI = APIFinder.findAPI(WizardAPI.class);
	}

	@Override
	public BoxHandlerResponse process(HttpServletRequest req, HttpServletResponse res, Box box, BoxBean bean) throws BoxProcessException {
		String command = box.getParameter1();
		String wizardId = box.getParameter2();
		if (StringUtils.isEmpty(command) || StringUtils.isEmpty(wizardId))
			throw new BoxProcessException("MissConfiguration, illegal command [" + command + "]  or wizardId [" + wizardId + "]");

		try {
			processRequest(command, wizardId);
		} catch (WizardAPIException e) {
			throw new BoxProcessException("Unsupported Command " + command);
		}
		return ResponseContinue.INSTANCE;
	}


	@Override
	public BoxHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, Box box) throws BoxSubmitException {
		String command = box.getParameter1();
		String wizardId = box.getParameter2();

		if (StringUtils.isEmpty(command) || StringUtils.isEmpty(wizardId))
			throw new BoxSubmitException("MissConfiguration, illegal command [" + command + "]  or wizardId [" + wizardId + "]");
		try {
			processRequest(command, wizardId);
		} catch (WizardAPIException e) {
			throw new BoxSubmitException("Unsupported Command " + command);
		}
		return ResponseContinue.INSTANCE;
	}

	/**
	 * Process method.
	 *
	 * @param command  configured command
	 * @param wizardId configured wizard id
	 * @throws WizardAPIException on {@link WizardAPI} errors
	 */
	private void processRequest(String command, String wizardId) throws WizardAPIException {
		WizardCommand wCommand = WizardCommand.getCommandByValue(command);
		switch (wCommand) {
			case NEXT:
				wizardAPI.allowNextStepNavigation(getWizard(wizardId), getCurrentStep(wizardId));
				break;
			case PREVIOUS:
				wizardAPI.allowPreviousStepNavigation(getWizard(wizardId), getCurrentStep(wizardId));
				break;
			case CANCEL:
				wizardAPI.allowWizardCancel(getWizard(wizardId), getCurrentStep(wizardId));
				break;
			case FINISH:
				wizardAPI.allowWizardFinish(getWizard(wizardId), getCurrentStep(wizardId));
				break;
			default:
				throw new IllegalArgumentException("Unsupported Command " + wCommand);
		}

	}


	/**
	 * Return {@link WizardStepAO}  current step.
	 *
	 * @param wizardId wizard id
	 * @return {@link WizardStepAO}
	 * @throws WizardAPIException on WizardAPI error
	 */

	private WizardStepAO getCurrentStep(String wizardId) throws WizardAPIException {
		if (StringUtils.isEmpty(wizardId))
			throw new IllegalArgumentException("Wizard Id is Illegal! Please check CMS configuration.");
		return wizardAPI.getCurrentStep(wizardId);
	}

	/**
	 * Return wizard with selected ID.
	 *
	 * @param wizardId wizard id
	 * @return {@link WizardAO}
	 * @throws WizardAPIException on WizardAPI error
	 */
	private WizardAO getWizard(String wizardId) throws WizardAPIException {
		if (StringUtils.isEmpty(wizardId))
			throw new IllegalArgumentException("Wizard Id is Illegal! Please check CMS configuration.");
		return wizardAPI.getWizard(wizardId);
	}


}
