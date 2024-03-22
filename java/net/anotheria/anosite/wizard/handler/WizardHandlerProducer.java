package net.anotheria.anosite.wizard.handler;

import net.anotheria.anosite.gen.aswizarddata.data.WizardDef;
import net.anotheria.anosite.util.AnositeConstants;
import net.anotheria.anosite.wizard.handler.response.WizardHandlerResponse;
import net.anotheria.anosite.wizard.handler.response.WizardResponseAbort;
import net.anotheria.moskito.core.calltrace.CurrentlyTracedCall;
import net.anotheria.moskito.core.calltrace.RunningTraceContainer;
import net.anotheria.moskito.core.calltrace.TraceStep;
import net.anotheria.moskito.core.calltrace.TracedCall;
import net.anotheria.moskito.core.predefined.ActionStats;
import net.anotheria.moskito.core.predefined.Constants;
import net.anotheria.moskito.core.producers.IStats;
import net.anotheria.moskito.core.producers.IStatsProducer;
import net.anotheria.moskito.core.registry.ProducerRegistryFactory;
import net.anotheria.moskito.core.stats.Interval;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Wizard handler producer, as Moskito IStatsProducer.
 *
 * @author h3ll
 */
public final class WizardHandlerProducer implements IStatsProducer {
	/**
	 * WizardHandlerProducer 'producerId'.
	 */
	private String producerId;
	/**
	 * WizardHandlerProducer 'preProcessStats'.
	 */
	private ActionStats preProcessStats;

	/**
	 * WizardHandlerProducer 'process'.
	 */
	private ActionStats process;
	/**
	 * WizardHandlerProducer 'submit'.
	 */
	private ActionStats submit;

	/**
	 * Cached list with stats.
	 */
	private final List<IStats> myStats;

	/**
	 * Handler Constructor.
	 *
	 * @param aProducerId id
	 */
	public WizardHandlerProducer(String aProducerId) {
		producerId = aProducerId;

		preProcessStats = new ActionStats("preProcess", getMonitoringIntervals());
		process = new ActionStats("process", getMonitoringIntervals());
		submit = new ActionStats("submit", getMonitoringIntervals());

		myStats = new ArrayList<IStats>();
		myStats.add(preProcessStats);
		myStats.add(process);
		myStats.add(submit);

		ProducerRegistryFactory.getProducerRegistryInstance().registerProducer(this);
	}

	protected Interval[] getMonitoringIntervals() {
		return Constants.getDefaultIntervals();
	}

	@Override
	public String getCategory() {
		return "wizard-handler";
	}

	@Override
	public String getProducerId() {
		return producerId;
	}

	@Override
	public List<IStats> getStats() {
		return myStats;
	}

	@Override
	public String getSubsystem() {
		return AnositeConstants.AS_MOSKITO_SUBSYSTEM;
	}

	/**
	 * PreProcess for Wizard.
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param wizard {@link WizardDef}
	 * @param target {@link WizardHandler}
	 * @return {@link WizardHandlerResponse}
	 */
	WizardHandlerResponse preProcess(HttpServletRequest req, HttpServletResponse res, WizardDef wizard, WizardHandler target) {
		preProcessStats.addRequest();
		long startTime = System.nanoTime();
		TracedCall aRunningUseCase = RunningTraceContainer.getCurrentlyTracedCall();
		TraceStep currentStep = null;
		CurrentlyTracedCall runningUseCase = aRunningUseCase.callTraced() ?
				(CurrentlyTracedCall) aRunningUseCase : null;
		if (runningUseCase != null)
			currentStep = runningUseCase.startStep(new StringBuilder(getProducerId()).append('.').append("preProcess").toString(), this, "preProcess");
		try {
			return target.preProcess(req, res, wizard);
		} catch (Exception e) {
			preProcessStats.notifyError();
			return new WizardResponseAbort(e);
		} finally {
			long duration = System.nanoTime() - startTime;
			preProcessStats.addExecutionTime(duration);
			preProcessStats.notifyRequestFinished();
			if (currentStep != null)
				currentStep.setDuration(duration);
			if (runningUseCase != null)
				runningUseCase.endStep();
		}

	}


	/**
	 * Process for Wizard.
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param wizard {@link WizardDef}
	 * @param target {@link WizardHandler}
	 * @return {@link WizardHandlerResponse}
	 */
	WizardHandlerResponse process(HttpServletRequest req, HttpServletResponse res, WizardDef wizard, WizardHandler target) {
		process.addRequest();
		long startTime = System.nanoTime();
		TracedCall aRunningUseCase = RunningTraceContainer.getCurrentlyTracedCall();
		TraceStep currentElement = null;
		CurrentlyTracedCall runningUseCase = aRunningUseCase.callTraced() ?
				(CurrentlyTracedCall) aRunningUseCase : null;
		if (runningUseCase != null)
			currentElement = runningUseCase.startStep(new StringBuilder(getProducerId()).append('.').append("process").toString(), this, "process");
		try {
			return target.process(req, res, wizard);
		} catch (Exception e) {
			process.notifyError();
			return new WizardResponseAbort(e);
		} finally {
			long duration = System.nanoTime() - startTime;
			process.addExecutionTime(duration);
			process.notifyRequestFinished();
			if (currentElement != null)
				currentElement.setDuration(duration);
			if (runningUseCase != null)
				runningUseCase.endStep();
		}

	}

	/**
	 * Submit for Wizard.
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param wizard {@link WizardDef}
	 * @param target {@link WizardHandler}
	 * @return {@link WizardHandlerResponse}
	 */
	WizardHandlerResponse submit(HttpServletRequest req, HttpServletResponse res, WizardDef wizard, WizardHandler target) {
		submit.addRequest();
		long startTime = System.nanoTime();
		TracedCall aRunningUseCase = RunningTraceContainer.getCurrentlyTracedCall();
		TraceStep currentElement = null;
		CurrentlyTracedCall runningUseCase = aRunningUseCase.callTraced() ?
				(CurrentlyTracedCall) aRunningUseCase : null;
		if (runningUseCase != null)
			currentElement = runningUseCase.startStep(new StringBuilder(getProducerId()).append('.').append("submit").toString(), this, "submit");
		try {
			return target.submit(req, res, wizard);
		} catch (Exception e) {
			submit.notifyError();
			return new WizardResponseAbort(e);
		} finally {
			long duration = System.nanoTime() - startTime;
			submit.addExecutionTime(duration);
			submit.notifyRequestFinished();
			if (currentElement != null)
				currentElement.setDuration(duration);
			if (runningUseCase != null)
				runningUseCase.endStep();
		}

	}


}
