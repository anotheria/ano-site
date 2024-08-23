package net.anotheria.anosite.wizard.handler;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.gen.aswizarddata.data.WizardDef;
import net.anotheria.anosite.wizard.WizardCommand;
import net.anotheria.anosite.wizard.api.WizardAO;
import net.anotheria.anosite.wizard.api.WizardAPI;
import net.anotheria.anosite.wizard.api.WizardStepAO;
import net.anotheria.anosite.wizard.api.exception.WizardAPIException;
import net.anotheria.anosite.wizard.api.exception.WizardAPIFirstStepException;
import net.anotheria.anosite.wizard.api.exception.WizardAPILastStepException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerPreProcessException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerProcessException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerSubmitException;
import net.anotheria.anosite.wizard.handler.response.WizardHandlerResponse;
import net.anotheria.anosite.wizard.handler.response.WizardResponseCancel;
import net.anotheria.anosite.wizard.handler.response.WizardResponseChangeStep;
import net.anotheria.anosite.wizard.handler.response.WizardResponseContinue;
import net.anotheria.anosite.wizard.handler.response.WizardResponseFinish;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static net.anotheria.anosite.handler.validation.AbstractValidationBoxHandler.REQ_PARAM_VALIDATION_ONLY;


/**
 * BaseWizardHandler as main class for Wizard handlers.
 * Used as Default WizardHandler for wizards where handler is not specified.
 * Can be simply extended.
 *
 * @author h3ll
 */
public class BaseWizardHandler implements WizardHandler {

	/**
	 * Log4j logger.
	 */
	private Logger log;
	/**
	 * WizardAPI instance.
	 */
	private WizardAPI wizardAPI;

	/**
	 * Constructor.
	 */
	protected BaseWizardHandler() {
		log = LoggerFactory.getLogger(this.getClass());
		wizardAPI = APIFinder.findAPI(WizardAPI.class);
	}

	@Override
	public WizardHandlerResponse preProcess(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerPreProcessException {
		// adding cache control headers
		addCacheControlHeadersToResponse(res);
		return WizardResponseContinue.INSTANCE;
	}


	@Override
	public WizardHandlerResponse process(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerProcessException {

		if (wizard.getWizardStepsSize() == 0)
			throw new WizardHandlerProcessException(wizard.getId(), " wizard Steps are not configured!");
		// adding cache control headers
		addCacheControlHeadersToResponse(res);
		try {
			WizardAO wizardAO = wizardAPI.getWizard(wizard.getId());
			WizardStepAO currentStep = wizardAPI.getCurrentStep(wizard.getId());

			return handleIncomingCommand(req, wizardAO, currentStep, false);
		} catch (WizardAPIException e) {
			log.error("WizardAPI failed", e);
			throw new WizardHandlerProcessException(e.getMessage(), e);
		} catch (WizardHandlerException e) {
			log.error("process failed", e);
			throw new WizardHandlerProcessException(e.getMessage(), e);
		}
	}


	@Override
	public WizardHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerSubmitException {

		try {
			WizardAO wizardAO = wizardAPI.getWizard(wizard.getId());
			WizardStepAO currentStep = wizardAPI.getCurrentStep(wizard.getId());

			return handleIncomingCommand(req, wizardAO, currentStep, true);
		} catch (WizardHandlerException e) {
			log.error("WizardAPI failed", e);
			throw new WizardHandlerSubmitException(e.getMessage(), e);
		} catch (WizardAPIException e) {
			log.error("submit failed", e);
			throw new WizardHandlerSubmitException(e.getMessage(), e);
		}

	}

	/**
	 * Returns true if we can be forwarded to next step. False otherwise.
	 *
	 * @param req	  {@link HttpServletRequest}
	 * @param wizard   {@link WizardAO}
	 * @param step	 {@link WizardStepAO}
	 * @param isSubmit boolean value - true if called from submit
	 * @return boolean value
	 */
	protected boolean isNextStepAllowed(HttpServletRequest req, WizardAO wizard, WizardStepAO step, boolean isSubmit) {
		return req.getParameter(REQ_PARAM_VALIDATION_ONLY) == null && isSubmit;
	}


	/**
	 * Returns true if we can be forwarded to previous step. False otherwise.
	 *
	 * @param req	  {@link HttpServletRequest}
	 * @param wizard   {@link WizardAO}
	 * @param step	 {@link WizardStepAO}
	 * @param isSubmit boolean value - true if called from submit
	 * @return boolean value
	 */
	protected boolean isPreviousStepAllowed(HttpServletRequest req, WizardAO wizard, WizardStepAO step, boolean isSubmit) {
		return !isSubmit;
	}


	/**
	 * Returns true if we can finish wizard.
	 *
	 * @param req	  {@link HttpServletRequest}
	 * @param wizard   {@link WizardAO}
	 * @param step	 {@link WizardStepAO}
	 * @param isSubmit boolean value - true if called from submit
	 * @return boolean value
	 */
	protected boolean isFinishAllowed(HttpServletRequest req, WizardAO wizard, WizardStepAO step, boolean isSubmit) {
		return req.getParameter(REQ_PARAM_VALIDATION_ONLY) == null && isSubmit;
	}

	/**
	 * Returns true if we can cancel wizard.
	 *
	 * @param req	  {@link HttpServletRequest}
	 * @param wizard   {@link WizardAO}
	 * @param step	 {@link WizardStepAO}
	 * @param isSubmit boolean value - true if called from submit
	 * @return boolean value
	 */
	protected boolean isCancellationAllowed(HttpServletRequest req, WizardAO wizard, WizardStepAO step, boolean isSubmit) {
		return !isSubmit;
	}


	/**
	 * Handle incoming command method.
	 * Checks which command should be executed, and execute it.
	 *
	 * @param req		 {@link HttpServletRequest}
	 * @param wizard	  {@link WizardDef}
	 * @param currentStep {@link WizardStepAO}
	 * @param isSubmit	boolean value - true if called from submit
	 * @return {@link WizardHandlerResponse}
	 * @throws net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerException
	 *          on errors
	 */
	protected WizardHandlerResponse handleIncomingCommand(HttpServletRequest req, WizardAO wizard, WizardStepAO currentStep,
														  boolean isSubmit) throws WizardHandlerException {

		@SuppressWarnings({"unchecked"})
		WizardCommand command = WizardCommand.getCommandByValue(req.getParameterMap());

		switch (command) {
			case FINISH:
				return processFinish(req, wizard, currentStep, isSubmit);
			case CANCEL:
				return processCancel(req, wizard, currentStep, isSubmit);
			case PREVIOUS:
				return processPreviousStep(req, wizard, currentStep, isSubmit);
			case NEXT:
				return processNextStep(req, wizard, currentStep, isSubmit);
			case NAVIGATE_TO:
				return processNavigateToStep(req, wizard, currentStep, isSubmit);

			default:
				getLogger().warn("Default Command - execution!");
				return processNextStep(req, wizard, currentStep, isSubmit);
		}
	}

	/**
	 * Process finish command.
	 *
	 * @param req		 {@link HttpServletRequest}
	 * @param wizard	  {@link WizardAO}
	 * @param currentStep {@link WizardStepAO}
	 * @param isSubmit	boolean flag
	 * @return {@link WizardHandlerResponse}
	 * @throws WizardHandlerException on errors from {@link WizardAPI}
	 */
	private WizardHandlerResponse processFinish(HttpServletRequest req, WizardAO wizard, WizardStepAO currentStep, boolean isSubmit) throws WizardHandlerException {
		if (!isFinishAllowed(req, wizard, currentStep, isSubmit)) {
			log.debug("Wizard finish {" + wizard.getId() + "} not performed");
			return WizardResponseContinue.INSTANCE;
		}
		log.debug("Wizard finished {" + wizard.getId() + "}");
		try {
			if (wizardAPI.finishWizard(wizard))
				return new WizardResponseFinish(wizard.getFinishRedirectUrl());

			log.debug("Wizard finish {" + wizard.getId() + "} not performed");
			return WizardResponseContinue.INSTANCE;
		} catch (WizardAPIException e) {
			log.error("WizardAPI failed", e);
			throw new WizardHandlerException(e.getMessage(), e);
		}
	}

	/**
	 * Process CancelCommand.
	 *
	 * @param req		 {@link HttpServletRequest}
	 * @param wizard	  {@link WizardAO}
	 * @param currentStep {@link WizardStepAO}
	 * @param isSubmit	boolean flag
	 * @return {@link WizardHandlerResponse}
	 * @throws WizardHandlerException on errors from {@link WizardAPI}
	 */
	private WizardHandlerResponse processCancel(HttpServletRequest req, WizardAO wizard, WizardStepAO currentStep, boolean isSubmit) throws WizardHandlerException {
		if (!isCancellationAllowed(req, wizard, currentStep, isSubmit)) {
			log.debug("Wizard cancel {" + wizard.getId() + "} not performed");
			return WizardResponseContinue.INSTANCE;
		}
		try {
			if (wizardAPI.cancelWizard(wizard))
				return new WizardResponseCancel(wizard.getCancelRedirectUrl());
			log.debug("Wizard cancel {" + wizard.getId() + "} not performed");
			return WizardResponseContinue.INSTANCE;
		} catch (WizardAPIException e) {
			log.error("WizardAPI failed", e);
			throw new WizardHandlerException(e.getMessage(), e);
		}
	}

	/**
	 * Process Previous step.
	 *
	 * @param req		 {@link HttpServletRequest}
	 * @param wizard	  {@link WizardAO}
	 * @param currentStep {@link WizardStepAO}
	 * @param isSubmit	boolean flag
	 * @return {@link WizardHandlerResponse}
	 * @throws WizardHandlerException on {@link WizardAPI} errors
	 */
	private WizardHandlerResponse processPreviousStep(HttpServletRequest req, WizardAO wizard, WizardStepAO currentStep, boolean isSubmit) throws WizardHandlerException {
		if (!isPreviousStepAllowed(req, wizard, currentStep, isSubmit)) {
			log.debug("Wizard previous step {" + wizard.getId() + "} not performed");
			return WizardResponseContinue.INSTANCE;
		}
		try {
			log.debug("Wizard previous step {" + wizard.getId() + "}");
			WizardStepAO changed = wizardAPI.adjustToPreviousStep(wizard.getId());
			//if step was changed!
			if (changed.equals(currentStep)) {
				//only logging!! Anyway changing step -  simply  to remove  back-step parameter!
				log.debug("Wizard previous step {" + wizard.getId() + "} not performed");
			}
			return WizardResponseChangeStep.INSTANCE;
		} catch (WizardAPIFirstStepException e) {
			return WizardResponseContinue.INSTANCE;
		} catch (WizardAPIException e) {
			log.error("WizardAPI failed", e);
			throw new WizardHandlerException(e.getMessage(), e);
		}
	}

	/**
	 * Handle navigate to command.
	 * Current method will allow navigation only to passed steps!!!
	 *
	 * @param req		 {@link HttpServletRequest}
	 * @param wizard	  {@link HttpServletResponse}
	 * @param currentStep {@link WizardStepAO}
	 * @param isSubmit	is submit operation
	 * @return {@link WizardHandlerResponse}
	 * @throws WizardHandlerException on errors
	 */
	private WizardHandlerResponse processNavigateToStep(HttpServletRequest req, WizardAO wizard, WizardStepAO currentStep, boolean isSubmit) throws WizardHandlerException {
		if (isSubmit) {
			log.warn("Menu rendering is disabled. NAVIGATE_TO can't be executed.");
			return WizardResponseContinue.INSTANCE;
		}

		String stepIndex = req.getParameter(WizardCommand.NAVIGATE_TO.getCommandTitle());
		if (StringUtils.isEmpty(stepIndex)) {
			log.warn("Required parameter not found.");
			return WizardResponseContinue.INSTANCE;
		}
		int stepNumber = Integer.valueOf(stepIndex);

		if (stepNumber < 0 || stepNumber >= wizard.getWizardSteps().size()) {
			log.debug("Illegal step " + stepNumber);
			return WizardResponseContinue.INSTANCE;
		}

		try {
			@SuppressWarnings({"UnusedDeclaration"})
			WizardStepAO nextStep = wizardAPI.adjustToStep(wizard.getId(), stepNumber);
			if (nextStep.equals(currentStep)) {
				//only logging!! Anyway changing step -  simply  to remove  back-step parameter!
				log.debug("step number is current");
			}
			return WizardResponseChangeStep.INSTANCE;

		} catch (WizardAPIException e) {
			log.error("WizardAPI failed", e);
			throw new WizardHandlerException("process navigateToStep failed  step:{" + stepIndex + "}", e);
		}

	}

	/**
	 * Process next step command.
	 *
	 * @param req		 {@link HttpServletRequest}
	 * @param wizard	  {@link WizardAO}
	 * @param currentStep {@link WizardStepAO}
	 * @param isSubmit	is submit
	 * @return {@link WizardHandlerResponse}
	 * @throws WizardHandlerException on errors
	 */
	private WizardHandlerResponse processNextStep(HttpServletRequest req, WizardAO wizard, WizardStepAO currentStep, boolean isSubmit)
			throws WizardHandlerException {
		if (!isNextStepAllowed(req, wizard, currentStep, isSubmit)) {
			log.debug("Wizard next step {" + wizard.getId() + "} not performed");
			return WizardResponseContinue.INSTANCE;
		}
		try {
			WizardStepAO changed = wizardAPI.adjustToNextStep(wizard.getId());
			if (changed.equals(currentStep)) {
				log.debug("Wizard next step {" + wizard.getId() + "} not performed");
				return WizardResponseContinue.INSTANCE;
			}
			return WizardResponseChangeStep.INSTANCE;
		} catch (WizardAPILastStepException e) {
			//Last step! trying to finish wizard
			try {
				if (wizardAPI.finishWizard(wizard))
					return new WizardResponseFinish(wizard.getFinishRedirectUrl());
				log.debug("Wizard finish  step {" + wizard.getId() + "} not performed");
				return WizardResponseContinue.INSTANCE;

			} catch (WizardAPIException e1) {
				log.error("WizardAPI failed", e);
				throw new WizardHandlerException(e1.getMessage(), e1);
			}
		} catch (WizardAPIException e) {
			log.error("WizardAPI failed", e);
			throw new WizardHandlerException(e.getMessage(), e);
		}


	}

	/**
	 * Current method add browser cache-control headers to response.
	 * Current will disable browser cache for all  wizard pages.
	 * <p/>
	 * Headers "Cache-Control", "Pragma", "Expires" with values. etc.
	 *
	 * @param response {@link HttpServletResponse}
	 */
	private void addCacheControlHeadersToResponse(HttpServletResponse response) {
		// "Cache-Control" header.
		response.setHeader("Cache-Control", "no-cache, no-store, post-check=0, pre-check=0");
		// "Pragma" header .
		response.setHeader("Pragma", "no-cache");
		// "Expires" header.
		response.setHeader("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");

	}


	/**
	 * Returns logger.
	 *
	 * @return {@link Logger}
	 */
	protected Logger getLogger() {
		return log;
	}

	/**
	 * Return {@link WizardAPI}.
	 *
	 * @return {@link WizardAPI}
	 */
	public WizardAPI getWizardAPI() {
		return wizardAPI;
	}


}
