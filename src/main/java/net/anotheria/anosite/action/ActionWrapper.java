package net.anotheria.anosite.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Action wrapper class.
 * Contains Moskito-Producer for selected action, and action itself.
 *
 * @author h3ll
 */
public class ActionWrapper implements Action {

	/**
	 * ActionWrapper action.
	 */
	private Action action;

	/**
	 * ActionWrapper producer.
	 */
	private ActionProducer producer;

	/**
	 * Constructor.
	 *
	 * @param action   {@link Action}
	 * @param producer {@link ActionProducer}
	 */
	public ActionWrapper(Action action, ActionProducer producer) {
		this.action = action;
		this.producer = producer;
	}

	@Override
	public ActionCommand execute(HttpServletRequest req, HttpServletResponse resp, ActionMapping mapping) throws Exception {
		return producer.execute(req, resp, mapping, action);
	}
}
