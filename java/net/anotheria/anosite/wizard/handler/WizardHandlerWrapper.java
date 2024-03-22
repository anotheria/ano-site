package net.anotheria.anosite.wizard.handler;

import net.anotheria.anosite.gen.aswizarddata.data.WizardDef;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerProcessException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerSubmitException;
import net.anotheria.anosite.wizard.handler.response.WizardHandlerResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Wrapper for Wizard handler.
 *
 * @author h3ll
 */
public class WizardHandlerWrapper implements WizardHandler {

	/**
	 * WizardHandlerWrapper 'handler'. {@link WizardHandler}
	 */
	private WizardHandler handler;
	/**
	 * WizardHandlerWrapper 'producer'. {@link WizardHandlerProducer}
	 */
	private WizardHandlerProducer producer;

	/**
	 * Constructor.
	 *
	 * @param aHandler  {@link WizardHandler}
	 * @param aProducer {@link WizardHandler}
	 */
	public WizardHandlerWrapper(WizardHandler aHandler, WizardHandlerProducer aProducer) {
		this.handler = aHandler;
		this.producer = aProducer;
	}


	@Override
	public WizardHandlerResponse preProcess(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) {
		return producer.preProcess(req, res, wizard, handler);
	}


	@Override
	public WizardHandlerResponse process(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerProcessException {
		return producer.process(req, res, wizard, handler);
	}

	@Override
	public WizardHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerSubmitException {
		return producer.submit(req, res, wizard, handler);
	}
}
