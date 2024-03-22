package net.anotheria.anosite.wizard.handler;

import net.anotheria.anosite.gen.aswizarddata.data.WizardDef;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerPreProcessException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerProcessException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerSubmitException;
import net.anotheria.anosite.wizard.handler.response.WizardHandlerResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Base, wizard (multi page dialog) Handler interface.
 *
 * @author h3ll
 */
public interface WizardHandler {

	/**
	 * Method called before wizard process execution.
	 *
	 * @param req	{@link jakarta.servlet.http.HttpServletRequest}}
	 * @param res	{@link jakarta.servlet.http.HttpServletResponse}
	 * @param wizard {@link net.anotheria.anosite.gen.aswizarddata.data.WizardDef}
	 * @return {@link WizardHandlerResponse}
	 * @throws WizardHandlerPreProcessException
	 *          on errors
	 */
	WizardHandlerResponse preProcess(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerPreProcessException;

	/**
	 * Process request to wizard.
	 *
	 * @param req	{@link jakarta.servlet.http.HttpServletRequest}}
	 * @param res	{@link jakarta.servlet.http.HttpServletResponse}
	 * @param wizard {@link net.anotheria.anosite.gen.aswizarddata.data.WizardDef}
	 * @return {@link WizardHandlerResponse}
	 * @throws net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerProcessException
	 *          on process errors
	 */
	WizardHandlerResponse process(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerProcessException;

	/**
	 * ProcessSubmit request to wizard.
	 *
	 * @param req	{@link jakarta.servlet.http.HttpServletRequest}}
	 * @param res	{@link jakarta.servlet.http.HttpServletResponse}
	 * @param wizard {@link net.anotheria.anosite.gen.aswizarddata.data.WizardDef}
	 * @return {@link WizardHandlerResponse}
	 * @throws net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerSubmitException
	 *          on submit errors
	 */
	WizardHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerSubmitException;

}
