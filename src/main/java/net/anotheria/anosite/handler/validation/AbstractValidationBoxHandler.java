package net.anotheria.anosite.handler.validation;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.handler.AbstractBoxHandler;
import net.anotheria.anosite.handler.BoxHandlerResponse;
import net.anotheria.anosite.handler.ResponseContinue;
import net.anotheria.anosite.handler.exception.BoxHandleException;
import net.anotheria.anosite.handler.exception.BoxProcessException;
import net.anotheria.anosite.handler.exception.BoxSubmitException;
import net.anotheria.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Abstract box handler for handling requests with validation functional.
 * 
 * @author Alexandr Bolbat
 */
public abstract class AbstractValidationBoxHandler<T extends AbstractFormBean> extends AbstractBoxHandler {

	/**
	 * JSON response encoding.
	 */
	public static final String JSON_RESPONSE_CODEPAGE = "UTF-8";

	/**
	 * JSON response content type.
	 */
	public static final String JSON_RESPONSE_CONTENT_TYPE = "text/x-json";

	/**
	 * Request parameter name for validation request only.
	 */
	public static final String REQ_PARAM_VALIDATION_ONLY = "validationOnly";

	/**
	 * Attribute postfix for storing form bean in request attribute.
	 */
	private static final String ATTR_FORM_BEAN = "formBean";

	/**
	 * Attribute postfix for storing validation response in request attribute.
	 */
	private static final String ATTR_VALIDATION_RESPONSE = "validationResponse";

	/**
	 * Attribute postfix for storing validation settings in request attribute.
	 */
	private static final String ATTR_VALIDATION_SETTINGS = "validationSettings";

	/**
	 * Attribute name for storing validation response string in request.
	 */
	public static final String ATTR_VALIDATION_RESPONSE_STRING = "vResponse";

	/**
	 * Attribute name for storing validation settings string in request.
	 */
	public static final String ATTR_VALIDATION_SETTINGS_STRING = "vSettings";

	@Override
	public final BoxHandlerResponse process(final HttpServletRequest req, final HttpServletResponse res, final Box box, final BoxBean bean)
			throws BoxProcessException {
		// executing "process" step for preparing required stuff for renderer
		BoxHandlerResponse response = executeProcess(req, res, box, bean);

		// preparing and publishing validation settings
		ValidationSettings vSettings = getFormValidationSettings(req);
		prepareFormValidationSettings(vSettings);
		req.setAttribute(ATTR_VALIDATION_SETTINGS_STRING, vSettings.toString());

		// publishing form data if this request fail on "submit" step on validation
		ValidationResponse vResponse = ValidationResponse.class.cast(req.getAttribute(getFormId() + ATTR_VALIDATION_RESPONSE));
		if (vResponse != null && vResponse.hasErrors()) {
			// preparing form data
			T formBean = getFormBean(req);
			// publishing form bean
			publishFormBean(formBean, req);
			// publishing validation response to request attribute for rendering later as HTML page part
			req.setAttribute(ATTR_VALIDATION_RESPONSE_STRING, vResponse.toString());
		}

		return response;
	}

	@Override
	public final BoxHandlerResponse submit(final HttpServletRequest req, final HttpServletResponse res, final Box box) throws BoxSubmitException {
		// preventing submit form id empty in getFormId method or without form id in request
		if (StringUtils.isEmpty(getFormId()) || StringUtils.isEmpty(req.getParameter(getFormId())))
			return ResponseContinue.INSTANCE;

		// validation form data
		ValidationResponse vResponse;
		try {
			vResponse = validate(req, res, box);
			req.setAttribute(getFormId() + ATTR_VALIDATION_RESPONSE, vResponse);
		} catch (BoxHandleException e) {
			throw new BoxSubmitException(e);
		}

		// writing JSON response if this request only for validation
		boolean isValidationOnly = req.getParameter(REQ_PARAM_VALIDATION_ONLY) != null;
		if (isValidationOnly)
			return writeJSONResponse(res, vResponse.toString());

		// submitting if no any validation errors
		if (!vResponse.hasErrors())
			return executeSubmit(req, res, box);

		// continue executing without real submitting because validation fail and need show errors after executing "process" step
		return ResponseContinue.INSTANCE;
	}

	/**
	 * Real process method for overriding in sub classes.
	 * 
	 * @param req request
	 * @param res response
	 * @param box box
	 * @param bean box bean
	 * @return {@link BoxHandlerResponse}
	 * @throws BoxProcessException  TODO dummy comment for javadoc.
	 */
	protected BoxHandlerResponse executeProcess(final HttpServletRequest req, final HttpServletResponse res, final Box box, final BoxBean bean)
			throws BoxProcessException {
		return super.process(req, res, box, bean);
	}

	/**
	 * Real submit method for overriding in sub classes.
	 * 
	 * @param req request
	 * @param res response
	 * @param box box
	 * @return {@link BoxHandlerResponse}
	 * @throws BoxSubmitException TODO dummy comment for javadoc.
	 */
	protected BoxHandlerResponse executeSubmit(final HttpServletRequest req, final HttpServletResponse res, final Box box) throws BoxSubmitException {
		return super.submit(req, res, box);
	}

	/**
	 * Prepare instant validation settings for current handler form.
	 * 
	 * @param vSettings
	 *            - request instant validation settings, this settings contains settings for every form in request
	 */
	protected void prepareFormValidationSettings(final ValidationSettings vSettings) {
	}

	/**
	 * Use this method if we want put some validation settings to page.
	 *
	 * @param req  TODO dummy comment for javadoc.
	 * @return {@link ValidationSettings}
	 */
	protected final ValidationSettings getFormValidationSettings(final HttpServletRequest req) {
		Object vSettingsObj = req.getAttribute(ATTR_VALIDATION_SETTINGS);
		if (vSettingsObj != null)
			return ValidationSettings.class.cast(vSettingsObj);

		ValidationSettings vSettings = ValidationSettings.create();
		req.setAttribute(ATTR_VALIDATION_SETTINGS, vSettings);
		return vSettings;
	}

	/**
	 * Get form bean. On first call to this method form bean will be created and prepared from request.
	 * @param req request
	 * @return T  TODO dummy comment for javadoc.
	 */
	protected final T getFormBean(final HttpServletRequest req) {
		Object beanObj = req.getAttribute(getFormId() + ATTR_FORM_BEAN);
		if (beanObj != null)
			return getFormBeanClass().cast(beanObj);

		T bean = prepareFormBean(createFormBean(), req);
		req.setAttribute(getFormId() + ATTR_FORM_BEAN, bean);
		return bean;
	}

	/**
	 * Publish form bean to request attributes.
	 * @param formBean form bean
	 * @param req request
	 */
	protected final void publishFormBean(final T formBean, final HttpServletRequest req) {
		for (String fieldName : formBean.getFieldsNames())
			req.setAttribute(fieldName, formBean.getFieldValue(fieldName));
	}

	/**
	 * Prepare form bean.
	 * 
	 * @param bean  TODO dummy comment for javadoc.
	 * @param req request.
	 * @return T  TODO dummy comment for javadoc.
	 * @throws BoxSubmitException  TODO dummy comment for javadoc.
	 */
	private final T prepareFormBean(final T bean, final HttpServletRequest req) {
		bean.prepare(req);
		return bean;
	}

	/**
	 * Create instance of form bean.
	 * 
	 * @return T  TODO dummy comment for javadoc.
	 */
	protected final T createFormBean() {
		try {
			return getFormBeanClass().newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Can't instantiate form bean.", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Can't instantiate form bean.", e);
		}
	}

	/**
	 * Form bean implementation class.
	 * 
	 * @return {@link Class} of T
	 */
	protected abstract Class<T> getFormBeanClass();

	/**
	 * Form id for this handler.
	 * 
	 * @return {@link String}
	 */
	protected abstract String getFormId();

	/**
	 * Validate submit request.
	 * 
	 * @param req  TODO dummy comment for javadoc.
	 * @param res  TODO dummy comment for javadoc.
	 * @param box  TODO dummy comment for javadoc.
	 * @return {@link ValidationResponse}
	 * @throws BoxHandleException  TODO dummy comment for javadoc.
	 */
	protected ValidationResponse validate(final HttpServletRequest req, final HttpServletResponse res, final Box box) throws BoxHandleException {
		return ValidationResponse.EMPTY_RESPONSE;
	}

	/**
	 * Writes JSON response and flush the stream.
	 * 
	 * @param res HttpServletResponse
	 * @param json response
	 * @return {@link BoxHandlerResponse}  TODO dummy comment for javadoc.
	 * @throws BoxSubmitException  TODO dummy comment for javadoc.
	 */
	protected final BoxHandlerResponse writeJSONResponse(final HttpServletResponse res, final String json) throws BoxSubmitException {
		res.setCharacterEncoding(JSON_RESPONSE_CODEPAGE);
		res.setContentType(JSON_RESPONSE_CONTENT_TYPE);
		try {
			PrintWriter writer = res.getWriter();
			writer.write(json);
			writer.flush();
			return ResponseContinue.INSTANCE;
		} catch (IOException e) {
			throw new BoxSubmitException(e);
		}
	}

}
