package net.anotheria.anosite.action;

import net.anotheria.anosite.util.AnositeConstants;
import net.anotheria.moskito.core.calltrace.CurrentlyTracedCall;
import net.anotheria.moskito.core.calltrace.RunningTraceContainer;
import net.anotheria.moskito.core.calltrace.TraceStep;
import net.anotheria.moskito.core.calltrace.TracedCall;
import net.anotheria.moskito.core.context.CurrentMeasurement;
import net.anotheria.moskito.core.context.MoSKitoContext;
import net.anotheria.moskito.core.predefined.ActionStats;
import net.anotheria.moskito.core.predefined.Constants;
import net.anotheria.moskito.core.producers.IStats;
import net.anotheria.moskito.core.producers.IStatsProducer;
import net.anotheria.moskito.core.registry.ProducerRegistryFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Moskito producer for Actions.
 *
 * @author h3ll
 */
public class ActionProducer implements IStatsProducer {

	/**
	 * ActionProducer producerId.
	 */
	private String producerId;
	/**
	 * Cached list with stats.
	 */
	private final List<IStats> actionStats;
	/**
	 * Stats for execute method.
	 */
	private ActionStats executeStats;

	/**
	 * Constructor.
	 *
	 * @param id producer id
	 */
	public ActionProducer(String id) {
		this.producerId = id;
		this.actionStats = new ArrayList<IStats>();
		this.executeStats = new ActionStats("execute", Constants.getDefaultIntervals());
		actionStats.add(executeStats);

		ProducerRegistryFactory.getProducerRegistryInstance().registerProducer(this);
	}


	@Override
	public List<IStats> getStats() {
		return actionStats;
	}

	@Override
	public String getProducerId() {
		return producerId;
	}

	@Override
	public String getCategory() {
		return "action";
	}

	@Override
	public String getSubsystem() {
		return AnositeConstants.AS_MOSKITO_SUBSYSTEM;
	}

	/**
	 * Execute method - which simply delegates call to real Action clazz, and manages Moskito-Stats!
	 *
	 * @param req	 {@link HttpServletRequest}
	 * @param resp	{@link HttpServletResponse}
	 * @param mapping {@link ActionMapping}
	 * @param action  {@link Action}
	 * @return {@link ActionCommand}
	 * @throws Exception on errors from original action
	 */
	protected ActionCommand execute(HttpServletRequest req, HttpServletResponse resp, ActionMapping mapping, Action action) throws Exception {

		MoSKitoContext moSKitoContext = MoSKitoContext.get();
		CurrentMeasurement cm = moSKitoContext.notifyProducerEntry(this);
		if (cm.isFirst()){
			cm.setCallDescription("execute "+mapping.toString());
		}

		executeStats.addRequest();
		long startTime = System.nanoTime();
		TracedCall aRunningUseCase = RunningTraceContainer.getCurrentlyTracedCall();
		TraceStep currentStep = null;
		CurrentlyTracedCall currentlyTracedCall = aRunningUseCase.callTraced() ?
				(CurrentlyTracedCall) aRunningUseCase : null;
		if (currentlyTracedCall != null)
			currentStep = currentlyTracedCall.startStep(new StringBuilder(getProducerId()).append('.').append("execute").toString(), this, "execute");
		try {
			return action.execute(req, resp, mapping);
		} catch (Exception e) {
			executeStats.notifyError();
			throw e;
		} finally {
			long duration = System.nanoTime() - startTime;

			moSKitoContext.notifyProducerExit(this);
			cm.notifyProducerFinished();

			executeStats.addExecutionTime(duration);
			executeStats.notifyRequestFinished();
			if (currentStep != null)
				currentStep.setDuration(duration);
			if (currentlyTracedCall != null)
				currentlyTracedCall.endStep();
		}
	}
}
