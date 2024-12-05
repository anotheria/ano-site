package net.anotheria.anosite.content.servlet;

import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anodoc.util.context.BrandConfig;
import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.session.APISessionImpl;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.access.AnoSiteAccessAPI;
import net.anotheria.anosite.access.AnoSiteAccessAPIException;
import net.anotheria.anosite.api.configuration.SystemConfigurationAPI;
import net.anotheria.anosite.config.ResourceDeliveryConfig;
import net.anotheria.anosite.content.bean.AttributeBean;
import net.anotheria.anosite.content.bean.AttributeMap;
import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.content.bean.BoxTypeBean;
import net.anotheria.anosite.content.bean.BreadCrumbItemBean;
import net.anotheria.anosite.content.bean.MediaLinkBean;
import net.anotheria.anosite.content.bean.NaviItemBean;
import net.anotheria.anosite.content.bean.PageBean;
import net.anotheria.anosite.content.bean.ScriptBean;
import net.anotheria.anosite.content.bean.SiteBean;
import net.anotheria.anosite.content.bean.StylesheetBean;
import net.anotheria.anosite.content.variables.VariablesUtility;
import net.anotheria.anosite.gen.asbrand.service.IASBrandService;
import net.anotheria.anosite.gen.asfeature.data.Feature;
import net.anotheria.anosite.gen.asfeature.service.ASFeatureServiceException;
import net.anotheria.anosite.gen.asfeature.service.IASFeatureService;
import net.anotheria.anosite.gen.asfederateddata.data.BoxType;
import net.anotheria.anosite.gen.asfederateddata.service.ASFederatedDataServiceException;
import net.anotheria.anosite.gen.asfederateddata.service.IASFederatedDataService;
import net.anotheria.anosite.gen.aslayoutdata.service.IASLayoutDataService;
import net.anotheria.anosite.gen.asresourcedata.data.FileLink;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.assitedata.data.MediaLink;
import net.anotheria.anosite.gen.assitedata.data.NaviItem;
import net.anotheria.anosite.gen.assitedata.data.PageAlias;
import net.anotheria.anosite.gen.assitedata.data.PageTemplate;
import net.anotheria.anosite.gen.assitedata.data.Script;
import net.anotheria.anosite.gen.assitedata.data.Site;
import net.anotheria.anosite.gen.assitedata.service.ASSiteDataServiceException;
import net.anotheria.anosite.gen.assitedata.service.IASSiteDataService;
import net.anotheria.anosite.gen.aswebdata.data.Attribute;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.gen.aswebdata.data.Pagex;
import net.anotheria.anosite.gen.aswebdata.data.PagexDocument;
import net.anotheria.anosite.gen.aswebdata.service.ASWebDataServiceException;
import net.anotheria.anosite.gen.aswebdata.service.IASWebDataService;
import net.anotheria.anosite.gen.aswizarddata.data.WizardDef;
import net.anotheria.anosite.gen.aswizarddata.service.ASWizardDataServiceException;
import net.anotheria.anosite.gen.aswizarddata.service.IASWizardDataService;
import net.anotheria.anosite.gen.shared.data.LinkTypesUtils;
import net.anotheria.anosite.gen.shared.data.MediaDescUtils;
import net.anotheria.anosite.guard.ConditionalGuard;
import net.anotheria.anosite.guard.GuardFactory;
import net.anotheria.anosite.handler.AbstractRedirectResponse;
import net.anotheria.anosite.handler.BoxHandler;
import net.anotheria.anosite.handler.BoxHandlerFactory;
import net.anotheria.anosite.handler.BoxHandlerResponse;
import net.anotheria.anosite.handler.ResponseAbort;
import net.anotheria.anosite.handler.ResponseContinue;
import net.anotheria.anosite.handler.ResponseRedirectAfterProcessing;
import net.anotheria.anosite.handler.ResponseRedirectImmediately;
import net.anotheria.anosite.handler.ResponseStop;
import net.anotheria.anosite.handler.exception.BoxHandleException;
import net.anotheria.anosite.localization.LocalizationEnvironment;
import net.anotheria.anosite.localization.LocalizationMap;
import net.anotheria.anosite.shared.AnositeConfig;
import net.anotheria.anosite.shared.InternalResponseCode;
import net.anotheria.anosite.shared.presentation.servlet.BaseAnoSiteServlet;
import net.anotheria.anosite.util.AnositeConstants;
import net.anotheria.anosite.wizard.api.WizardAPI;
import net.anotheria.anosite.wizard.api.exception.WizardAPIException;
import net.anotheria.anosite.wizard.handler.WizardHandler;
import net.anotheria.anosite.wizard.handler.WizardHandlerFactory;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerProcessException;
import net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerSubmitException;
import net.anotheria.anosite.wizard.handler.response.WizardHandlerResponse;
import net.anotheria.anosite.wizard.handler.response.WizardResponseAbort;
import net.anotheria.anosite.wizard.handler.response.WizardResponseCancel;
import net.anotheria.anosite.wizard.handler.response.WizardResponseChangeStep;
import net.anotheria.anosite.wizard.handler.response.WizardResponseContinue;
import net.anotheria.anosite.wizard.handler.response.WizardResponseFinish;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.moskito.core.blueprint.BlueprintCallExecutor;
import net.anotheria.moskito.core.blueprint.BlueprintProducer;
import net.anotheria.moskito.core.blueprint.BlueprintProducersFactory;
import net.anotheria.moskito.webui.util.VersionUtil;
import net.anotheria.util.IdCodeGenerator;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import net.anotheria.util.maven.MavenVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.anotheria.anosite.util.AnositeConstants.*;

/**
 * This servlet builds and delivers pages (out of pagexs objects) and is therefore one of the main classes in the ano-site framework.
 *
 * @author lrosenberg
 */
public class ContentPageServlet extends BaseAnoSiteServlet {

	/**
	 * {@link Logger} instance.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(ContentPageServlet.class);

	/**
	 * Basic, serialVersionUID.
	 */
	private static final long serialVersionUID = -2697998321193246382L;

	/**
	 * Parameter which allows to override the title stored in the page object.
	 */
	public static final String OVERRIDE_PAGE_TITLE = "CP.OverridePageTitle";
	/**
	 * If set more output will be produced by jsps. Useful in development, but should be removed in production environment. Configured via configureme.
	 */
	public static final String BEAN_ANOSITE_VERBOSITY = "anosite.verbose";
	/**
	 * Charset for files on filesystem.
	 */
	public static final Charset MY_FS_CHARSET = Charset.forName("UTF-8"/*"ISO-8859-15"*/);	
	/**
	 * Resource delivering configuration.
	 */
	private static final ResourceDeliveryConfig rdConfig = ResourceDeliveryConfig.getInstance();
	/**
	 * File path part constant.
	 */
	private static final String FILE_PATH_PART = "/file/";

	/**
	 * HTML suffix.
	 */
	private static final String HTML_SUFFIX = ".html";
	/**
	 * Wizard suffix.
	 */
	private static final String W_HTML_SUFFIX = ".whtml";

	/**
	 * WebDataService for boxes and pages.
	 */
	private transient IASWebDataService webDataService;
	/**
	 * Site service for layout and site objects.
	 */
	private transient IASSiteDataService siteDataService;
	/**
	 * Federated data.
	 */
	private transient IASFederatedDataService federatedDataService;
	/**
	 * Layout data.
	 */
	private transient IASLayoutDataService layoutDataService;
	/**
	 * Resources service.
	 */
	private transient IASResourceDataService resourceDataService;

	/**
	 * Feature service.
	 */
	private transient IASFeatureService featureService;

	/**
	 * Wizard service.
	 */
	private transient IASWizardDataService wizardDataService;
	/**
	 * {@link IASBrandService} instance.
	 */
	private transient IASBrandService brandService;

	/**
	 * WizardAPI instance.
	 */
	private transient WizardAPI wizardAPI;
	/**
	 * BlueprintCallExecutor pageExecutor, for creating and handling Pages with built In moskito stats.
	 */
	private transient BlueprintCallExecutor pageExecutor;
	/**
	 * BlueprintCallExecutor boxExecutor, for creating and handling Boxes with built In moskito stats.
	 */
	private transient BlueprintCallExecutor boxExecutor;

	/**
	 * BlueprintCallExecutor wizardExecutor.
	 */
	private transient BlueprintCallExecutor wizardExecutor;
	/**
	 * Configuration instance.
	 */
	private AnositeConfig config = AnositeConfig.getInstance();
	/**
	 * {@link SimpleDateFormat}.
	 */
	private static SimpleDateFormat generatedFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
	/**
	 * {@link IdBasedLockManager} instance.
	 */
	private transient IdBasedLockManager lockManager;
	
	/**
	 * {@link AnoSiteAccessAPI} instance.
	 */
	private AnoSiteAccessAPI accessAPI;
	/**
	 * API: {@link SystemConfigurationAPI}.
	 */
	private transient SystemConfigurationAPI systemConfigurationAPI;

	private String page404 = "404.html";

	private String page500 = "500.html";


	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		LOGGER.info("Init ContentPageServlet");
		try {
			webDataService = MetaFactory.get(IASWebDataService.class);
			siteDataService = MetaFactory.get(IASSiteDataService.class);
			federatedDataService = MetaFactory.get(IASFederatedDataService.class);
			layoutDataService = MetaFactory.get(IASLayoutDataService.class);
			resourceDataService = MetaFactory.get(IASResourceDataService.class);
			wizardDataService = MetaFactory.get(IASWizardDataService.class);
			featureService    = MetaFactory.get(IASFeatureService.class);
			brandService = MetaFactory.get(IASBrandService.class);
			wizardAPI = APIFinder.findAPI(WizardAPI.class);
			accessAPI = APIFinder.findAPI(AnoSiteAccessAPI.class);
			systemConfigurationAPI = APIFinder.findAPI(SystemConfigurationAPI.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "Init ASG services failure", e);
			throw new ServletException("Init ASG services failure", e);
		}
		pageExecutor = new PageBeanCreator();
		boxExecutor = new BoxBeanCreator();
		wizardExecutor = new WizardExecutor();
		config.getServletContext().setAttribute(AnositeConstants.AA_ANOSITE_RANDOM, IdCodeGenerator.generateCode(10));

        page404 = config.getInitParameter("page404");
        if (StringUtils.isEmpty(page404)) {
            page404 = "404.html";
        }
        page500 = config.getInitParameter("page500");
        if (StringUtils.isEmpty(page500)) {
            page500 = "500.html";
        }
        //Lock!
        lockManager = new SafeIdBasedLockManager();
    }

	@Override
	protected void moskitoDoGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			process(req, res, false);
		} catch (ASGRuntimeException e) {
			LOGGER.error("moskitoDoGet", e);
			throw new ServletException("ASG Runtime Exception: " + e.getMessage());
		} catch (BoxHandleException e) {
			LOGGER.error("moskitoDoGet", e);
			throw new ServletException("Box Handle Exception: " + e.getMessage());
		} catch (WizardHandlerException e) {
			LOGGER.error("moskitoDoPost", e);
			throw new ServletException("Wizard Handle Exception: " + e.getMessage());
		}
	}

	@Override
	protected void moskitoDoPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			process(req, res, true);
		} catch (ASGRuntimeException e) {
			LOGGER.error("moskitoDoPost", e);
			throw new ServletException("ASG Runtime Exception: " + e.getMessage());
		} catch (BoxHandleException e) {
			LOGGER.error("moskitoDoPost", e);
			throw new ServletException("Box Handle Exception: " + e.getMessage());
		} catch (WizardHandlerException e) {
			LOGGER.error("moskitoDoPost", e);
			throw new ServletException("Wizard Handle Exception: " + e.getMessage());
		}
	}

	/**
	 * If we working with wizards - we Should obtain IDBased lock - before processing (to avoid double submits, etc).
	 * In other  case  - lock - won't be obtained.
	 * TODO : it's actually temp solution which will be removed!
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param submit boolean value
	 * @throws ServletException	on errors
	 * @throws IOException		 on input output errors
	 * @throws ASGRuntimeException on backend failures
	 * @throws net.anotheria.anosite.handler.exception.BoxHandleException
	 *                             on box handle errors
	 * @throws net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerException
	 *                             on wizard handle errors
	 */
	private void process(HttpServletRequest req, HttpServletResponse res, boolean submit) throws ServletException, IOException, ASGRuntimeException,
			BoxHandleException, WizardHandlerException {

		IdBasedLock lock = null;
		try {
			boolean isWizardRequest = isRequestToResource(req, W_HTML_SUFFIX);
			if (isWizardRequest) {
				String sId = APICallContext.getCallContext().getCurrentSession().getId();
				final String lockId = extractPageName(req) + "_" + sId;
				lock = lockManager.obtainLock(lockId);
				lock.lock();
			}
			processRequest(req, res, submit);
		} finally {
			if (lock != null)
				lock.unlock();
		}
	}

	/**
	 * Processes the incoming request.
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param submit true if the request was submitted via post (for calling submit in handlers)
	 * @throws ServletException	on errors
	 * @throws IOException		 on input output errors
	 * @throws ASGRuntimeException on backend failures
	 * @throws net.anotheria.anosite.handler.exception.BoxHandleException
	 *                             on box handle errors
	 * @throws net.anotheria.anosite.wizard.handler.exceptions.WizardHandlerException
	 *                             on wizard handle errors
	 */

	protected void processRequest(HttpServletRequest req, HttpServletResponse res, boolean submit) throws ServletException, IOException, ASGRuntimeException, BoxHandleException, WizardHandlerException {

		prepareTextResources(req);
		req.setAttribute(BEAN_ANOSITE_VERBOSITY, config.verbose() ? Boolean.TRUE : Boolean.FALSE);

		String requestURI = req.getRequestURI();
		String queryString = req.getQueryString();
		if (queryString == null || queryString.length() == 0)
			requestURI += "?dummy=dummy";
		else
			requestURI += "?" + queryString;
		req.setAttribute(AnositeConstants.RA_CURRENT_URI, requestURI);

		checkSwitchMode(req);

		//setting proper error code, because we shouldn't deliver error pages with http code 200.
		String pageName = extractPageName(req);
		boolean errorPage = pageName != null && (pageName.equals("404") || pageName.equals("500"));
		if (errorPage) {
			setErrorCode(pageName, res);
		}

		//checking if HTML suffix present
		boolean isPageRequest = isRequestToResource(req, HTML_SUFFIX);

		//checking if .whtml suffix present
		boolean isWizardRequest = isRequestToResource(req, W_HTML_SUFFIX);

		//initing Pagex if request was to it (trying to resolve page even if .html suffix not present)
		Pagex page = isPageRequest || !isWizardRequest ? resolvePageByName(pageName) : null;
		//initing wizard if request was not to page
		WizardDef wizard = page == null && isWizardRequest ? getWizardByName(pageName) : null;

		boolean handleWizard = wizard != null;

		//wizard initialization.
		if (page == null && handleWizard) {
			try {
				//looking for wizard current page id!
				//if first call - first wizard page will be used
				String wizardPageId = wizardAPI.getCurrentStepPageId(wizard.getId());
				try {
					if (!StringUtils.isEmpty(wizardPageId))
						page = webDataService.getPagex(wizardPageId);
				} catch (ASWebDataServiceException e) {
					LOGGER.trace("ignore", e);
				}
			} catch (WizardAPIException e) {
				LOGGER.trace("ignore", e);
			}

		}


		if (page == null) {
			//ok, page not found, first we are trying to fall back to file system.
			boolean foundOnFs = !submit && fallBackToFileSystem(req, res);
			if (foundOnFs)
				return;
			//we have an error
			//if (isPageRequest && page == null) {
			if (errorPage)
				throw new ServletException("Page: " + pageName + " not found.");
			APICallContext.getCallContext().getCurrentSession().setAttribute("404.sourcePageName", pageName);
			res.sendRedirect(req.getContextPath() + "/" + page404);
			return;
		}

		if (wizard == null) {
			wizard = getCurrentPageWizard(page);
			//found case
			if (wizard != null) {
				res.sendRedirect(req.getContextPath() + "/" + wizard.getName() + W_HTML_SUFFIX);
				return;
			}
		}

        //set page name to session.
        if (page != null) {
            APICallContext.getCallContext().getCurrentSession().setAttribute(SA_PAGE_NAME, page.getName());
        }

        //does we need to redirect request to localized page name.
        boolean redirectToLocalizedPage = page != null && !StringUtils.isEmpty(page.getLocalizedName()) && !page.getLocalizedName().equals(pageName);

        if (redirectToLocalizedPage) {
            String urlQuery = req.getQueryString();
            urlQuery = urlQuery != null && urlQuery.length() > 0 ? "?" + urlQuery : "";
            String pageUrl = page.getLocalizedName()+HTML_SUFFIX + urlQuery;
            res.sendRedirect(pageUrl);
            return;
        }

        //new check for https.
		boolean secure = req.isSecure();
		//ANOSITE-14, added config.httpsOnly.
		boolean secureRequired = (page.getHttpsonly() && config.enforceHttps()) || (config.httpsOnly());

		if (!secure && secureRequired) {
			String redirectTarget = "https://";
			redirectTarget += req.getServerName();
			redirectTarget += requestURI;
			redirectTarget = URLEncoder.encode(redirectTarget, "UTF-8");
			LOGGER.debug("making secure switch to " + redirectTarget);
			res.sendRedirect(redirectTarget);
			return;
		}

		if (secure && !secureRequired && config.enforceHttp()) {
			String redirectTarget = "http://";
			redirectTarget += req.getServerName();
			redirectTarget += requestURI;
			redirectTarget = URLEncoder.encode(redirectTarget, "UTF-8");
			LOGGER.debug("making unsecure switch to " + redirectTarget);
			res.sendRedirect(redirectTarget);
			return;
		}

		// end check for https --->


		//copy attributes from action scope if any 
		Map<String, Object> actionScope = ((APISessionImpl) APICallContext.getCallContext().getCurrentSession()).getActionScope();

		for (String key : actionScope.keySet())
			req.setAttribute(key, actionScope.get(key));

		((APISessionImpl) APICallContext.getCallContext().getCurrentSession()).resetActionScope();
		// end action scope 

		PageTemplate template = siteDataService.getPageTemplate(page.getTemplate());
		prepareTemplateLocalization(template.getLocalizations());

		HashMap<String, BoxHandler> handlerCache = new HashMap<String, BoxHandler>();
		if (!submit) {
			if ("true".equals(req.getParameter("submitFlag")))
				submit = true;

		}
		try {
			if (submit) {
				// /// Box submit
				// checking access
				if (!accessAPI.isAllowedForPage(page.getId())) {
					res.sendRedirect(req.getContextPath() + "/" + "403.html");
					return;
				}

				InternalResponse response = processSubmit(req, res, page, template, handlerCache);
				if (response.getCode() == InternalResponseCode.CONTINUE_AND_REDIRECT) {
					res.sendRedirect(InternalRedirectResponse.class.cast(response).getUrl());
					return;
				}

				if (!response.canContinue())
					return;
				// /// Box submit End

				// wizard submit part
				if (handleWizard) {
					// checking access
					if (!accessAPI.isAllowedForWizard(wizard.getId())) {
						res.sendRedirect(req.getContextPath() + "/" + "403.html");
						return;
					}

					response = processSubmitWizard(req, res, wizard);
					if (response.getCode() == InternalResponseCode.CONTINUE_AND_REDIRECT) {
						res.sendRedirect(InternalRedirectResponse.class.cast(response).getUrl());
						return;
					}
				}

				// TODO any further actions needed?
				if (!response.canContinue())
					return;
				// wizard submit part
			}
		} catch (AnoSiteAccessAPIException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServletException(e);
		}

		//ok, if we got sofar, we have at least continue and error or continue responses.

		//wizard create!
		if (handleWizard) {
			//////////////Wizard start //////////////
			try {
				// checking access
				if (!accessAPI.isAllowedForWizard(wizard.getId())) {
					res.sendRedirect(req.getContextPath() + "/" + "403.html");
					return;
				}
				
				BlueprintProducer wizardProducer = BlueprintProducersFactory.getBlueprintProducer("Wizard-" + wizard.getId() + "-" + wizard.getName(), "wizard",
						AnositeConstants.AS_MOSKITO_SUBSYSTEM);
				InternalResponse wizardResponse = InternalResponse.class.cast(wizardProducer.execute(wizardExecutor, req, res, wizard));
				if (wizardResponse.getCode() == InternalResponseCode.ABORT) {
					res.sendRedirect(req.getContextPath() + "/" + page500);
				}

				if (!wizardResponse.canContinue()) {
					LOGGER.debug("Wizard " + wizard + " response can't continue");
					return;
				}
			} catch (Exception e) {
				LOGGER.error("Could not handle WizardDef with ID: " + wizard.getId(), e);
				throw new ASGRuntimeException("Could not create WizardBean for WizardDef with ID:" + wizard.getId() + ": " + e.getMessage() + "! See logs for more details.");
			}
		}   //////////////Wizard end //////////////


		//set the proper stylesheet
		req.setAttribute("stylesheet", new StylesheetBean(layoutDataService.getPageLayout(template.getLayout()).getStyle()));

		SiteBean siteBean = createSiteBean(template, req);
		req.setAttribute("site", siteBean);

		///////////////// PAGE START ////////////////////
		InternalResponse pageResponse;
		try {
			// checking access
			if (!accessAPI.isAllowedForPage(page.getId() )) {
				res.sendRedirect(req.getContextPath() + "/" + "403.html");
				return;
			}
			
			BlueprintProducer pageProducer = BlueprintProducersFactory.getBlueprintProducer("Page-" + page.getId() + "-" + page.getName(), "page", AnositeConstants.AS_MOSKITO_SUBSYSTEM);
			pageResponse = (InternalResponse) pageProducer.execute(pageExecutor, req, res, page, template);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServletException(e);
		}
		///////////////// PAGE END //////////////////////

		if (pageResponse.getCode() == InternalResponseCode.ABORT) {
			res.sendRedirect(req.getContextPath() + "/" + page500);
		}

		if (!pageResponse.canContinue()) {
			LOGGER.warn("Page "+page.getName()+" ["+page.getId()+"] pageResponse " + pageResponse + " response can't continue");
			return;
		}

		String titleOverride = (String) req.getAttribute(OVERRIDE_PAGE_TITLE);

		PageBean pageBean = ((InternalPageBeanResponse) pageResponse).getPageBean();
		pageBean.setGenerated(generatedFormat.format(page.getLastUpdateTimestamp()));
		if (pageBean.getTitle() == null || pageBean.getTitle().length() == 0)
			pageBean.setTitle(siteBean.getTitle());
		if (titleOverride != null && titleOverride.length() > 0)
			pageBean.setTitle(titleOverride);

		if (pageBean.getKeywords() == null || pageBean.getKeywords().length() == 0)
			pageBean.setKeywords(siteBean.getKeywords());
		if (pageBean.getDescription() == null || pageBean.getDescription().length() == 0)
			pageBean.setDescription(siteBean.getDescription());

		req.setAttribute("page", pageBean);

		// prepare navi
		Site site = siteDataService.getSite(template.getSite());
		List<NaviItemBean> topNavi = createNaviItemList(site.getTopNavi(), req);
		req.setAttribute("topNavi", topNavi);
		req.setAttribute("topNaviSize", topNavi.size());

		List<NaviItemBean> mainNavi = createNaviItemList(site.getMainNavi(), req);
		req.setAttribute("mainNavi", mainNavi);
		req.setAttribute("mainNaviSize", mainNavi.size());
		// navi end

		//prepare breadcrumb
		List<BreadCrumbItemBean> breadcrumb = prepareBreadcrumb(page, site);
		req.setAttribute("breadcrumbs", breadcrumb);

		//execute the final redirect
		if (pageResponse.getCode() == InternalResponseCode.CONTINUE_AND_REDIRECT) {
			String redirectUrl = ((InternalPageBeanWithRedirectResponse) pageResponse).getRedirectUrl();
			res.sendRedirect(redirectUrl);
			return;
		}

		String pageLayout = template.getLayout();
		String layoutPage = layoutDataService.getPageLayout(pageLayout).getLayoutpage();
		if (!layoutPage.startsWith("/"))
			layoutPage = "/net/anotheria/anosite/layout/templates/" + layoutPage;
		if (!layoutPage.endsWith(".jsp"))
			layoutPage += ".jsp";
		if (!"true".equals(req.getParameter(AnositeConstants.FLAG_XML_REQUEST)) && !res.isCommitted()) {
			RequestDispatcher dispatcher = req.getRequestDispatcher(layoutPage);
			dispatcher.forward(req, res);
		}


	}

	/**
	 * Check SwitchMode parameter in current URI.
	 * ANO-JIRA-> CMS-30 issue.
	 *
	 * @param req {@link HttpServletRequest}
	 */
	private void checkSwitchMode(HttpServletRequest req) {
		String switchModeParameter = req.getParameter(PARAM_SWITCH_MODE);
		if (StringUtils.isEmpty(switchModeParameter))
			return;

		if (switchModeParameter.equals(PARAM_VALUE_EDIT_MODE))
			req.getSession().setAttribute(SA_EDIT_MODE_FLAG, Boolean.TRUE);
		if (switchModeParameter.equals(PARAM_VALUE_VIEW_MODE))
			req.getSession().removeAttribute(SA_EDIT_MODE_FLAG);
	}

	/**
	 * Check's whether requestUri ends with .html.
	 * BY default - if URL does not contains '.' - true will be returned.
	 *
	 * @param req			{@link HttpServletRequest}
	 * @param resourcePrefix resource prefix {.html,.whtml}
	 * @return boolean value
	 */
	private boolean isRequestToResource(HttpServletRequest req, String resourcePrefix) {
		if (StringUtils.isEmpty(resourcePrefix))
			return false;
		final boolean defaultValue = true;
		final String requestUrl = req.getRequestURI();
		if (StringUtils.isEmpty(requestUrl))
			return defaultValue;
		int indexOfDot = requestUrl.indexOf('.');
		if (indexOfDot > 0 && indexOfDot != requestUrl.length() - 1)
			return resourcePrefix.equals(requestUrl.substring(indexOfDot, requestUrl.length()));
		return defaultValue;
	}

	/**
	 * Returns {@link WizardDef} to which current{@link Pagex} belongs, if such exist.
	 * Otherwise null.
	 * Means that current page can be step of some withard - in this case withard will be returned.
	 *
	 * @param page {@link Pagex}
	 * @return {@link WizardDef}
	 */
	private WizardDef getCurrentPageWizard(Pagex page) {
		try {
			List<WizardDef> wizards = wizardDataService.getWizardDefs();
			for (WizardDef wiz : wizards) {
				for (String pageId : wiz.getWizardSteps())
					if (page.getId().equals(pageId))
						return wiz;
			}
		} catch (ASWizardDataServiceException e) {
			LOGGER.trace("ignored", e);
		}
		return null;
	}


	/**
	 * Process submit for Wizard.
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param wizard {@link WizardDef}
	 * @return {@link InternalResponse}
	 * @throws WizardHandlerSubmitException on submit errors
	 */
	private InternalResponse processSubmitWizard(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerSubmitException {
		WizardHandler handler = WizardHandlerFactory.createHandler(wizard.getHandler());

		WizardHandlerResponse response = handler.submit(req, res, wizard);
		switch (response.getResponseCode()) {
			case CONTINUE:
				return new InternalResponseContinue();
			case CONTINUE_AND_REDIRECT:
				if (response instanceof WizardResponseFinish)
					return new InternalRedirectResponse(WizardResponseFinish.class.cast(response).getRedirectUrl());
				if (response instanceof WizardResponseChangeStep) {
					//redirect to self!
					return new InternalRedirectResponse(req.getContextPath() + req.getRequestURI());
				}
				LOGGER.warn("wizard  " + wizard + " trying to rewrite redirect, denied");
				return new InternalResponseContinue();
			case CANCEL_AND_REDIRECT:
				if (response instanceof WizardResponseCancel)
					try {
						res.sendRedirect(WizardResponseCancel.class.cast(response).getRedirectUrl());
					} catch (IOException e) {
						LOGGER.error("Redirect failed, target: ", e);
					}
				//abort execution
				return new InternalResponse(InternalResponseCode.STOP);
			case STOP:
				return new InternalResponse(InternalResponseCode.STOP);
			case ABORT:
				if (response instanceof WizardResponseAbort) {
					WizardResponseAbort abort = WizardResponseAbort.class.cast(response);
					@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
					Exception cause = abort != null ? abort.getCause() : null;
					String message = abort != null ? abort.getCauseMessage() : null;
					throw new WizardHandlerSubmitException("Execution aborted " + message != null ? message : "", cause != null ? cause : new RuntimeException("No Exception " +
							"given"));
				}

				throw new RuntimeException("Execution aborted: " + response);
			default:
				throw new AssertionError("Unexpected case in response: " + response.getResponseCode());
		}

	}

	/**
	 * Trying to find wizard with selected name. If not found null will be returned.
	 *
	 * @param wizardName name itself
	 * @return {@link WizardDef}
	 */
	private WizardDef getWizardByName(String wizardName) {
		try {
			List<WizardDef> wizards = wizardDataService.getWizardDefsByProperty(WizardDef.PROP_NAME, wizardName);
			if (wizards == null || wizards.isEmpty()) {
				LOGGER.debug("Withards are not  configured!");
				return null;
			}

			return wizards.get(0);
		} catch (ASWizardDataServiceException e) {
			LOGGER.trace(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Setting error CODE.
	 *
	 * @param pageName pageName
	 * @param res	  {@link HttpServletResponse}
	 */
	private void setErrorCode(String pageName, HttpServletResponse res) {
		int errorCode = pageName == null ? HttpServletResponse.SC_NOT_FOUND : 0;
		errorCode = pageName != null && pageName.equals("404") ? HttpServletResponse.SC_NOT_FOUND :
				pageName != null && pageName.equals("500") ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : errorCode;
		res.setStatus(errorCode);
	}

	/**
	 * Returns the ids of the boxes for the given rendering step and rendered page.
	 * As for now there are 13 rendering steps:
	 * template meta, template header, page header, template c1first, template c2 first, template c3 first,
	 * page c1, page c2, page c3,
	 * template c1last, template c2 last, template c3 last, template footer, page footer.
	 *
	 * @param page	 {@link Pagex}
	 * @param template {@link PageTemplate}
	 * @param step	 current step
	 * @return {@link List<String>}
	 */
	private List<String> getBoxIdsForRenderingStep(Pagex page, PageTemplate template, int step) {
		switch (step) {
			case 0:
				return template.getMeta();
			case 1:
				return template.getHeader();
			case 2:
				return page.getHeader();
			case 3:
				return template.getC1first();
			case 4:
				return template.getC2first();
			case 5:
				return template.getC3first();
			case 6:
				return page.getC1();
			case 7:
				return page.getC2();
			case 8:
				return page.getC3();
			case 9:
				return template.getC1last();
			case 10:
				return template.getC2last();
			case 11:
				return template.getC3last();
			case 12:
				return template.getFooter();
			case 13:
				return page.getFooter();
		}
		throw new RuntimeException("Error, step " + step + " is unknown!");
	}

	/**
	 * If the request was a POST or the submit flag was set, the request is sent through this function where each handler for each box is instantiated and submit is called.
	 *
	 * @param req		  {@link HttpServletRequest}
	 * @param res		  {@link HttpServletResponse}
	 * @param page		 {@link Pagex}
	 * @param template	 {@link PageTemplate}
	 * @param handlerCache {@link Map<String,BoxHandler>}
	 * @return {@link InternalResponse}
	 * @throws ASGRuntimeException on backend failures
	 * @throws net.anotheria.anosite.handler.exception.BoxHandleException
	 *                             on box handle errors
	 */
	private InternalResponse processSubmit(HttpServletRequest req, HttpServletResponse res, Pagex page, PageTemplate template, HashMap<String, BoxHandler> handlerCache) throws ASGRuntimeException, BoxHandleException {

		InternalResponse progress = new InternalResponseContinue();
		int step = 0;
		while (progress.canContinue() && step < 14) {
			progress = processSubmit(req, res, getBoxIdsForRenderingStep(page, template, step), handlerCache, progress);
			step++;
		}
		LOGGER.debug("Returning at step: " + step + " : " + progress);
		return progress;


	}

	/**
	 * Called by above process submit action for each list of boxes in the page.
	 *
	 * @param req		  {@link HttpServletRequest}
	 * @param res		  {@link HttpServletResponse}
	 * @param boxIds	   {@link List<String>}
	 * @param handlerCache {@link Map<String,BoxHandler>}
	 * @param previous	 {@link InternalResponse} previous step response
	 * @return {@link InternalResponse}
	 * @throws ASGRuntimeException on errors
	 * @throws net.anotheria.anosite.handler.exception.BoxHandleException
	 *                             on box handle errors
	 */
	private InternalResponse processSubmit(HttpServletRequest req, HttpServletResponse res, List<String> boxIds, HashMap<String, BoxHandler> handlerCache, InternalResponse previous) throws ASGRuntimeException, BoxHandleException {
		if (boxIds == null || boxIds.size() == 0)
			return previous;
		boolean doRedirect = false;
		String redirectTarget = null;
		for (String id : boxIds) {
			Box box = webDataService.getBox(id);

			if (disabledByGuards(req, box))
				continue;

			String handlerId = box.getHandler();


			if (handlerId != null && handlerId.length() > 0) {
				BoxHandler handler = BoxHandlerFactory.createHandler(handlerId);
				BoxHandlerResponse response = handler.submit(req, res, box);
				switch (response.getResponseCode()) {
					case CONTINUE:
						break;
					case CONTINUE_AND_REDIRECT:
						if (!(previous instanceof InternalRedirectResponse)) {
							doRedirect = true;
							redirectTarget = ((AbstractRedirectResponse) response).getRedirectTarget();
						} else {
							LOGGER.warn("box " + box + " trying to rewrite redirect, denied");
						}
						break;
					case CANCEL_AND_REDIRECT:
						redirectTarget = ((AbstractRedirectResponse) response).getRedirectTarget();
						try {
							res.sendRedirect(redirectTarget);
						} catch (IOException e) {
							LOGGER.error("Redirect failed, target: " + redirectTarget, e);
						}
						//abort execution
						return new InternalResponse(InternalResponseCode.STOP);
					case STOP:
						return new InternalResponse(InternalResponseCode.STOP);
					case ABORT:
						@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
						Exception e = ((ResponseAbort) response).getCause();
						if (e == null)
							throw new RuntimeException("No exception given");
						if (e instanceof RuntimeException)
							throw (RuntimeException) e;
						throw new RuntimeException("Execution aborted: " + e.getMessage() + " (" + e.getClass()+")", e);
					default:
						throw new AssertionError("Unexpected case in response: " + response.getResponseCode());

				}
				handlerCache.put(box.getId(), handler);
			}
			List<String> subboxesIds = box.getSubboxes();
			InternalResponse subResponse = processSubmit(req, res, subboxesIds, handlerCache, previous);
			//a redirect from subbox can override a continue from upper box
			if (subResponse.getCode() == InternalResponseCode.CONTINUE_AND_REDIRECT && previous.getCode() == InternalResponseCode.CONTINUE)
				previous = subResponse;
			if (!subResponse.canContinue())
				return new InternalResponse(InternalResponseCode.STOP);

		}
		if (doRedirect) {
			return new InternalRedirectResponse(redirectTarget);
		}
		return previous;
	}


	/**
	 * Creates a breadcrumb (users path along the navigation) for sites that supports it.
	 *
	 * @param page {@link Pagex}
	 * @param site {link Site}
	 * @return {@link List<BreadCrumbItemBean>}
	 */
	private List<BreadCrumbItemBean> prepareBreadcrumb(Pagex page, Site site) {
		List<BreadCrumbItemBean> ret = new ArrayList<BreadCrumbItemBean>();
		APISession session = APICallContext.getCallContext().getCurrentSession();

		BrowsingHistory naviHistory = (BrowsingHistory) session.getAttribute("as.history.navi");
		if (naviHistory == null) {
			naviHistory = new BrowsingHistory();
			session.setAttribute("as.history.navi", naviHistory);
		}


		try {
			//first find navi item
			List<NaviItem> linkingItems = siteDataService.getNaviItemsByProperty(NaviItem.LINK_PROP_INTERNAL_LINK, page.getId());
			if (linkingItems.size() == 0)
				return ret;
			NaviItem linkingItem = linkingItems.get(0);

			naviHistory.addHistoryItem(linkingItem.getId());

			if (site.getStartpage().length() > 0) {
				linkingItems = siteDataService.getNaviItemsByProperty(NaviItem.LINK_PROP_INTERNAL_LINK, site.getStartpage());
				NaviItem linkToStartPage = linkingItems.get(0);

				BreadCrumbItemBean startpageBean = new BreadCrumbItemBean();
				startpageBean.setTitle(linkToStartPage.getName());

				String sLink = webDataService.getPagex(linkToStartPage.getInternalLink()).getName() + HTML_SUFFIX;

				// aliased link to startpage
				if (linkToStartPage.getPageAlias().length() > 0) {
					String pageAliasId = linkToStartPage.getPageAlias();
					PageAlias alias = siteDataService.getPageAlias(pageAliasId);
					sLink = alias.getName();
					if (sLink.startsWith("/")) {
						sLink = sLink.substring(1, sLink.length());
					}
				}
				startpageBean.setLink(sLink);

				ret.add(startpageBean);
				if (linkToStartPage.equals(linkingItem)) {
					startpageBean.setClickable(false);
					return ret;
				} else {
					startpageBean.setClickable(true);
				}
			}

			//ok start page is found and we are not the startPage
			//now find other... this is now hardcoded for two level navigation.
			List<BreadCrumbItemBean> items = new ArrayList<BreadCrumbItemBean>();
			while (linkingItem != null) {
				BreadCrumbItemBean b = new BreadCrumbItemBean();
				b.setClickable(items.size() > 0);
				b.setTitle(linkingItem.getName());
				try {
					LOGGER.info(linkingItem.getName() + ":" + linkingItem.getInternalLink() + "," + linkingItem.getExternalLink());
					String link = webDataService.getPagex(linkingItem.getInternalLink()).getName() + HTML_SUFFIX;

					// aliased link to a page
					if (linkingItem.getPageAlias().length() > 0) {
						String pageAliasId = linkingItem.getPageAlias();
						PageAlias alias = siteDataService.getPageAlias(pageAliasId);
						link = alias.getName();
						if (link.startsWith("/")) {
							link = link.substring(1, link.length());
						}
					}

					// external Link
					if (linkingItem.getExternalLink().length() > 0) {
						link = linkingItem.getExternalLink();
					}
					b.setLink(link);
				} catch (NoSuchDocumentException e) {
					b.setLink("");
					b.setClickable(false);
				}
				items.add(b);
				String searchId = linkingItem.getId();
				linkingItem = null;
				List<NaviItem> tosearch = siteDataService.getNaviItems();

				String previousNaviItemId = naviHistory.getPreviousItem();

				if (previousNaviItemId != null) {
					for (NaviItem i : tosearch) {
						//System.out.println("checking "+i);
						if (i.getSubNavi().contains(searchId)) {
						}

						if (i.getSubNavi().contains(searchId) && i.getId().equals(previousNaviItemId)) {
							linkingItem = i;
							break;
						}
					}
				}

				if (linkingItem == null) {
					for (NaviItem i : tosearch) {
						if (i.getSubNavi().contains(searchId)) {
							linkingItem = i;
							break;
						}
					}
				}
			}

			Collections.reverse(items);
			ret.addAll(items);


		} catch (Exception e) {
			BreadCrumbItemBean b = new BreadCrumbItemBean();
			b.setTitle("Error: " + e.getMessage());
			b.setClickable(false);
			ret.add(b);
		}

		return ret;
	}

	/**
	 * Creates the bean for a single box.
	 *
	 * @param req {@link HttpServletRequest}
	 * @param res {@link HttpServletResponse}
	 * @param box {@link Box}
	 * @return {@link InternalResponse}
	 * @throws ASGRuntimeException on errors
	 * @throws net.anotheria.anosite.handler.exception.BoxHandleException
	 *                             on processErrors
	 */
	private InternalResponse createBoxBean(HttpServletRequest req, HttpServletResponse res, Box box) throws ASGRuntimeException, BoxHandleException {

		BoxBean ret = new BoxBean();
		APICallContext.getCallContext().setAttribute(BoxBean.CALL_CONTEXT_SCOPE_NAME, ret);

		ret.setName(box.getName());
		ret.setId(box.getId());
		ret.setCssClass(box.getCssClass());

		ret.setMediaLinks(createMediaLinkBeanList(box.getMediaLinks(), req));
		ret.setScripts(createScriptBeanList(box.getScripts(), req));

		AttributeMap attributeMap = createAttributeMap(req, res, box.getAttributes());
		APICallContext.getCallContext().setAttribute(AttributeMap.BOX_ATTRIBUTES_CALL_CONTEXT_SCOPE_NAME, attributeMap);
		ret.setAttributes(attributeMap);

		prepareBoxLocalization(ret, box.getLocalizations());
		ret.setContent(VariablesUtility.replaceVariables(req, box.getContent()));
		ret.setParameter1(VariablesUtility.replaceVariables(req, box.getParameter1()));
		ret.setParameter2(VariablesUtility.replaceVariables(req, box.getParameter2()));
		ret.setParameter3(VariablesUtility.replaceVariables(req, box.getParameter3()));
		ret.setParameter4(VariablesUtility.replaceVariables(req, box.getParameter4()));
		ret.setParameter5(VariablesUtility.replaceVariables(req, box.getParameter5()));
		ret.setParameter6(VariablesUtility.replaceVariables(req, box.getParameter6()));
		ret.setParameter7(VariablesUtility.replaceVariables(req, box.getParameter7()));
		ret.setParameter8(VariablesUtility.replaceVariables(req, box.getParameter8()));
		ret.setParameter9(VariablesUtility.replaceVariables(req, box.getParameter9()));
		ret.setParameter10(VariablesUtility.replaceVariables(req, box.getParameter10()));

		ret.setType(createBoxTypeBean(box.getType()));

		BoxHandlerResponse handlerResponse = null;

		// Firsts notify handler
		if (box.getHandler() != null && box.getHandler().length() > 0) {
			BoxHandler handler = BoxHandlerFactory.createHandler(box.getHandler());
			handlerResponse = handler.process(req, res, box, ret);
		}

		if (handlerResponse == null)
			handlerResponse = new ResponseContinue();

		if (handlerResponse.getResponseCode() == InternalResponseCode.CANCEL_AND_REDIRECT) {
			try {
				res.sendRedirect(((ResponseRedirectImmediately) handlerResponse).getRedirectTarget());
			} catch (IOException e) {
				LOGGER.warn("Couldn't send redirect to " + ((ResponseRedirectImmediately) handlerResponse).getRedirectTarget() + ", aborting execution.", e);
			}
			handlerResponse = new ResponseStop();
		}

		InternalResponse response = null;

		switch (handlerResponse.getResponseCode()) {
			case ERROR_AND_CONTINUE://TODO make an error bean later
			case CONTINUE:
				response = new InternalBoxBeanResponse(InternalResponseCode.CONTINUE, ret);
				break;
			case CONTINUE_AND_REDIRECT:
				response = new InternalBoxBeanWithRedirectResponse(ret, ((ResponseRedirectAfterProcessing) handlerResponse).getRedirectTarget());
				break;
			case STOP:
				response = new InternalResponse(handlerResponse);
				break;
			case ABORT:
				//noinspection UnusedAssignment
				response = new InternalResponse(handlerResponse);
				//FIX: BoxHanler.process exceptions logging
				Exception e = ((ResponseAbort) handlerResponse).getCause();
				LOGGER.error("createBoxBean() for Box[" + box.getId() + "] failure: ", e);
				throw new ASGRuntimeException(e);
		}

		if (response == null) {
			LOGGER.error("Response is null!");
			throw new RuntimeException("Unhandled handler response: " + handlerResponse);
		}

		if (!response.canContinue())
			return response;

		// Then create subboxes
		if (box.getSubboxes() != null && box.getSubboxes().size() > 0) {
			InternalResponse subBoxResponse = createBoxBeanList(req, res, box.getSubboxes());
			switch (subBoxResponse.getCode()) {
				case ERROR_AND_CONTINUE:
				case CONTINUE:
					ret.setSubboxes(((InternalBoxBeanListResponse) subBoxResponse).getBeans());
					break;
				case STOP:
					return subBoxResponse;
				case ABORT:
					return subBoxResponse;
				case CONTINUE_AND_REDIRECT:
					ret.setSubboxes(((InternalBoxBeanListResponse) subBoxResponse).getBeans());
					if (response.getCode() != InternalResponseCode.CONTINUE_AND_REDIRECT)
						response = new InternalBoxBeanWithRedirectResponse(ret, ((InternalBoxBeanListWithRedirectResponse) subBoxResponse).getRedirectUrl());
					break;
			}
		}

		return response;
	}

	/**
	 * Returns true if the box is disabled by conditional guards and should be ignored.
	 *
	 * @param req {@link HttpServletRequest}
	 * @param box {@link Box}
	 * @return boolean value
	 */
	private boolean disabledByGuards(HttpServletRequest req, Box box) {
		//check the guards
		List<String> gIds = box.getGuards();
		for (String gid : gIds) {
			ConditionalGuard g = null;
			try {
				g = GuardFactory.getConditionalGuard(gid);
				if (!g.isConditionFullfilled(box, req)) {
					return true;
				}

			} catch (Exception e) {
				LOGGER.warn("Caught error in guard processing ( guard: " + g + ", gid: " + gid + ", boxid: " + box.getId() + ")", e);
			}
		}
		return false;
	}

	/**
	 * Returns true if the box is disabled by conditional guards and should be ignored.
	 *
	 * @param req {@link HttpServletRequest}
	 * @param box {@link Box}
	 * @return boolean value
	 */
	private boolean disabledByFeature(HttpServletRequest req, Box box) {
		//check the feature
		String featureId = box.getFeature();
		if (featureId==null || featureId.length()==0)
			return false;
		Feature feature = null;
		try{
			feature = featureService.getFeature(featureId);
		}catch(ASFeatureServiceException e){
			return false;
		}catch(NoSuchDocumentException e){
			return false;
		}

		if (!feature.getEnabled())
			return true;

		/* check if this feature must be worked on production */
		if (systemConfigurationAPI.getCurrentSystem().startsWith("PROD") && !feature.getActiveInProduction())
			return true;

		List<String> gIds = feature.getGuards();
		for (String gid : gIds) {
			ConditionalGuard g = null;
			try {
				g = GuardFactory.getConditionalGuard(gid);
				if (!g.isConditionFullfilled(box, req)) {
					return true;
				}

			} catch (Exception e) {
				LOGGER.warn("Caught error in guard processing ( guard: " + g + ", gid: " + gid + ", boxid: " + box.getId() + ")", e);
			}
		}
		return false;
	}
	/**
	 * Returns true if the box is disabled by conditional guards and should be ignored.
	 *
	 * @param req {@link HttpServletRequest}
	 * @param mediaLink {@link MediaLink}
	 * @return boolean value
	 */
	private boolean disabledByGuards(HttpServletRequest req, MediaLink mediaLink) {
		//check the guards
		List<String> gIds = mediaLink.getGuards();
		for (String gid : gIds) {
			ConditionalGuard g = null;
			try {
				g = GuardFactory.getConditionalGuard(gid);
				if (!g.isConditionFullfilled(mediaLink, req)) {
					return true;
				}

			} catch (Exception e) {
				LOGGER.warn("Caught error in guard processing ( guard: " + g + ", gid: " + gid + ", mediaLinkId: " + mediaLink.getId() + ")", e);
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the box is disabled by conditional guards and should be ignored.
	 *
	 * @param req {@link HttpServletRequest}
	 * @param script {@link Script}
	 * @return boolean value
	 */
	private boolean disabledByGuards(HttpServletRequest req, Script script) {
		//check the guards
		List<String> gIds = script.getGuards();
		for (String gid : gIds) {
			ConditionalGuard g = null;
			try {
				g = GuardFactory.getConditionalGuard(gid);
				if (!g.isConditionFullfilled(script, req)) {
					return true;
				}

			} catch (Exception e) {
				LOGGER.warn("Caught error in guard processing ( guard: " + g + ", gid: " + gid + ", scriptId: " + script.getId() + ")", e);
			}
		}
		return false;
	}

	/**
	 * Creates a {@link List<BoxBean>} for corresponding boxId collection.
	 * Note! Boxes that are guarded by conditional guards will be ignored if the guards say so.
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param boxIds {@link List<String>}
	 * @return {@link InternalResponse}
	 * @throws ASGRuntimeException on errors
	 */
	private InternalResponse createBoxBeanList(HttpServletRequest req, HttpServletResponse res, List<String> boxIds) throws ASGRuntimeException {
		ArrayList<BoxBean> ret = new ArrayList<BoxBean>();
		String redirectUrl = null;
		for (String boxId : boxIds) {

			Box box = webDataService.getBox(boxId);
			
			// checking access
			try {
				if (!accessAPI.isAllowedForBox(box.getId()))
					continue;
			} catch (Exception e) {
				LOGGER.warn("Error in AccessAPI. Box: " + box + ", BoxId: " + boxId + ")", e);
			}

			if (disabledByGuards(req, box))
				continue;

			if (disabledByFeature(req, box))
				continue;
			/// END GUARDS HANDLING ///

			InternalResponse response = null;
			BlueprintProducer boxProducer = BlueprintProducersFactory.getBlueprintProducer("Box-" + box.getId() + "-" + box.getName(), "box", AnositeConstants.AS_MOSKITO_SUBSYSTEM);
			try {
				response = (InternalResponse) boxProducer.execute(boxExecutor, req, res, box);
			} catch (Exception e) {
				LOGGER.error("Could not create BoxBean for Box with ID: " + box.getId(), e);
				throw new ASGRuntimeException("Could not create BoxBean for Box with ID: " + box.getId() + ": " + e.getMessage() + "! See logs for more details.");
			}


			if (!response.canContinue())
				return response;
			ret.add(((InternalBoxBeanResponse) response).getBean());
			if (response.getCode() == InternalResponseCode.CONTINUE_AND_REDIRECT && redirectUrl == null)
//				redirectUrl = ((InternalBoxBeanListWithRedirectResponse)response).getRedirectUrl();
				//Fixing of the class casting bug that is occurred when BoxHandler.process() return RedirectAfterProcessing
				redirectUrl = ((InternalBoxBeanWithRedirectResponse) response).getRedirectUrl();
		}

		return redirectUrl == null ?
				new InternalBoxBeanListResponse(ret) :
				new InternalBoxBeanListWithRedirectResponse(ret, redirectUrl);
	}

	/**
	 * Creates the box type bean for the boxTypeId.
	 *
	 * @param boxTypeId string id of {@link BoxType}
	 * @return {@link BoxTypeBean}
	 * @throws ASFederatedDataServiceException
	 *          on errors
	 */
	private BoxTypeBean createBoxTypeBean(String boxTypeId) throws ASGRuntimeException {
		BoxType type = null;
		if (!StringUtils.isEmpty(boxTypeId)) {
			type = federatedDataService.getBoxType(boxTypeId);
		} else {
			LOGGER.debug("BoxType is not defined. Using \"Plain\" as default.");
			List<BoxType> types = federatedDataService.getBoxTypesByProperty(BoxType.PROP_NAME, "Plain");
			if (types.size() != 1) {
				LOGGER.debug("Could not use default BoxType with name \"Plain\": either it doesn't exist or duplicated!");
				throw new ASGRuntimeException("BoxType is not defined! Please set property \"type\" of Box in the CMS.");
			}
			type = types.get(0);
		}
		BoxTypeBean bean = new BoxTypeBean();
		bean.setName(type.getName());
		bean.setRenderer(type.getRendererpage());
		return bean;
	}

	/**
	 * Creates list of NaviItemBeans for all navi item objects in the list.
	 *
	 * @param idList {@link List<String>} list of navi id's
	 * @param req	{@link HttpServletRequest}
	 * @return {@link List<NaviItemBean>}
	 * @throws ASSiteDataServiceException on site data service errors
	 * @throws ASWebDataServiceException  on web data service errors
	 */
	private List<NaviItemBean> createNaviItemList(List<String> idList, HttpServletRequest req) throws ASSiteDataServiceException, ASWebDataServiceException, ASResourceDataServiceException {
		List<NaviItemBean> ret = new ArrayList<NaviItemBean>(idList.size());
		for (String id : idList) {
			NaviItemBean bean = new NaviItemBean();
			boolean do_break = false;

			NaviItem item = null;
			try{
				item = siteDataService.getNaviItem(id);
			}catch(NoSuchDocumentException nsde){
				LOGGER.warn("Couldn't retrieve navi item with id " + id + ", ignored.");
				//leave the loop
				break;
			}
			
			// checking access
			try {
				if (!accessAPI.isAllowedForNaviItem(item.getId()))
					continue;
			} catch (Exception e) {
				LOGGER.warn("Error in AccessAPI. NaviItem: " + item + ", NaviItemId: " + id + ")", e);
			}

			//check the guards
			List<String> gIds = item.getGuards();
			for (String gid : gIds) {
				ConditionalGuard g = null;
				try {
					g = GuardFactory.getConditionalGuard(gid);
					if (!g.isConditionFullfilled(item, req)) {
						do_break = true;
						break;
					}
				} catch (Exception e) {
					LOGGER.warn("Error in guard, caught (guard: " + g + ", gid: " + gid + ", naviitem: " + item + ", itemId: " + id + ")", e);
				}

			}

			if (do_break) {
				continue;
			}

			bean.setPopup(item.getPopup());
			bean.setName(item.getName());
			bean.setTitle(item.getTitle());
			bean.setIcon(item.getIcon());
			bean.setClassName(item.getClassName());

			// internal link to a page
			if (item.getInternalLink().length() > 0) {
				String pageId = item.getInternalLink();
				Pagex page = webDataService.getPagex(pageId);
                String pageLocalizedName = page.getLocalizedName();
                String pageName = StringUtils.isEmpty(pageLocalizedName) ? page.getName() : pageLocalizedName;
				bean.setLink(pageName + HTML_SUFFIX);
				if (extractPageName(req).equals(pageName))
					bean.setSelected(true);
			} else {
				bean.setLink("#");
			}

			// aliased link to a page
			if (item.getPageAlias().length() > 0) {
				String pageAliasId = item.getPageAlias();
				PageAlias alias = siteDataService.getPageAlias(pageAliasId);
				String link = alias.getName();
				if (link.startsWith("/")) {
					link = link.substring(1, link.length());
				}
				bean.setLink(link);
			}

			// external link
			if (item.getExternalLink().length() > 0) {
				bean.setLink(item.getExternalLink());
			}


			ret.add(bean);
			List<String> subNaviIds = item.getSubNavi();
			if (subNaviIds.size() > 0) {
				List<NaviItemBean> subNavi = createNaviItemList(subNaviIds, req);
				if (!bean.isSelected())
					for (NaviItemBean subBean : subNavi)
						if (subBean.isSelected()) {
							bean.setSelected(true);
							break;
						}
				bean.setSubNavi(subNavi);
			}
		}

		return ret;
	}

	/**
	 * Return page redirect target if possible, null otherwise.
	 *
	 * @param response			   {@link HttpServletResponse}
	 * @param previousRedirectTarget string previous redirect target
	 * @return redirect target or null
	 */
	private String getNewPageRedirectTargetIfApplies(InternalResponse response, String previousRedirectTarget) {
		if (previousRedirectTarget != null)
			return previousRedirectTarget;
		if (response.getCode() == InternalResponseCode.CONTINUE_AND_REDIRECT)
			return ((InternalBoxBeanListWithRedirectResponse) response).getRedirectUrl();
		return null;
	}

	/**
	 * Returns previous InternalResponse in case of CONTINUE_AND_REDIRECT code, current otherwise.
	 *
	 * @param current  {@link InternalResponse}
	 * @param previous {@link InternalResponse}
	 * @return previous InternalResponse in case of CONTINUE_AND_REDIRECT code, current otherwise.
	 */
	private InternalResponse getNewPageResponse(InternalResponse current, InternalResponse previous) {
		if (previous.getCode() == InternalResponseCode.CONTINUE_AND_REDIRECT)
			return previous;
		return current;
	}

	/**
	 * Create wizard method.
	 *
	 * @param req	{@link HttpServletRequest}
	 * @param res	{@link HttpServletResponse}
	 * @param wizard {@link WizardDef}
	 * @return {@link InternalResponse}
	 * @throws WizardHandlerProcessException on errors
	 */
	private InternalResponse handleWizardProcess(HttpServletRequest req, HttpServletResponse res, WizardDef wizard) throws WizardHandlerException {
		WizardHandler handler = WizardHandlerFactory.createHandler(wizard.getHandler());

		//first execute pre-process
		WizardHandlerResponse response = handler.preProcess(req, res, wizard);
		response = response == null ? WizardResponseContinue.INSTANCE : response;
		InternalResponse result = handleWizardProcessResponse(req, res, wizard, response);
		if (!result.canContinue()) {
			LOGGER.debug("wizard pre-process :  RESPONSE can't continue. Process won't be executed");
			return result;
		}
		//execute process
		response = handler.process(req, res, wizard);
		response = response == null ? WizardResponseContinue.INSTANCE : response;
		return handleWizardProcessResponse(req, res, wizard, response);
	}

	/**
	 * Handle response of wizard pre-process and process.
	 *
	 * @param req	  {@link HttpServletRequest}
	 * @param res	  {@link HttpServletResponse}
	 * @param wizard   {@link WizardDef}
	 * @param response {@link WizardHandlerResponse}
	 * @return {@link InternalResponse} as processed response
	 * @throws WizardHandlerProcessException on errors
	 */
	private InternalResponse handleWizardProcessResponse(HttpServletRequest req, HttpServletResponse res, WizardDef wizard, WizardHandlerResponse response) throws WizardHandlerException {
		switch (response.getResponseCode()) {
			case ERROR_AND_CONTINUE://TODO make an error bean later (same as for BOX - create)
				return new InternalResponseContinue();
			case CONTINUE:
				return new InternalResponseContinue();
			case CONTINUE_AND_REDIRECT:
				if (response instanceof WizardResponseFinish)
					return new InternalRedirectResponse(WizardResponseFinish.class.cast(response).getRedirectUrl());
				// WizardResponseChangeStep on process means  that we should not Continue rendering
				if (response instanceof WizardResponseChangeStep) {
					try {
						//redirect to self! immediately!!!!
						res.sendRedirect(req.getContextPath() + req.getRequestURI());
					} catch (IOException e) {
						LOGGER.error("Redirect failed, target: ", e);
					}
					//abort execution
					return new InternalResponse(InternalResponseCode.STOP);
				}
				LOGGER.warn("wizard  " + wizard + " trying to rewrite redirect, denied");
				return new InternalResponseContinue();
			case CANCEL_AND_REDIRECT:
				if (response instanceof WizardResponseCancel)
					try {
						res.sendRedirect(WizardResponseCancel.class.cast(response).getRedirectUrl());
					} catch (IOException e) {
						LOGGER.error("Redirect failed, target: ", e);
					}
				//abort execution
				return new InternalResponse(InternalResponseCode.STOP);
			case STOP:
				return new InternalResponse(InternalResponseCode.STOP);
			case ABORT:
				if (response instanceof WizardResponseAbort) {
					WizardResponseAbort abort = WizardResponseAbort.class.cast(response);
					@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
					Exception cause = abort != null ? abort.getCause() : null;
					String message = abort != null ? abort.getCauseMessage() : null;
					LOGGER.error("Could not create wizardBean for wizard{" + wizard.getId() + "}" + message != null ? message : "", cause);
					throw new WizardHandlerException("Execution aborted " + message != null ? message : "", cause != null ? cause : new RuntimeException("No Exception " +
							"given"));
				}

				throw new RuntimeException("Execution aborted: " + response);
			default:
				throw new AssertionError("Unexpected case in response: " + response.getResponseCode());
		}
	}


	/**
	 * Creates the page bean which represents part of current page for rendering.
	 *
	 * @param req	  {@link HttpServletResponse}
	 * @param res	  {@link HttpServletRequest}
	 * @param page	 {@link Pagex}
	 * @param template {@link PageTemplate}
	 * @return {@link InternalResponse}
	 * @throws ASGRuntimeException on errors
	 */
	private InternalResponse createPageBean(HttpServletRequest req, HttpServletResponse res, Pagex page, PageTemplate template) throws ASGRuntimeException {
		preparePageLocalization(page.getLocalizations());
		BrandConfig brandConfig = ContextManager.getCallContext().getBrandConfig();

		if (brandConfig != null) {
			prepareBrandLocalization(brandConfig.getLocalizations());
		}

		PageBean ret = new PageBean();

		ret.setTitle(VariablesUtility.replaceVariables(req, page.getTitle()));
		ret.setKeywords(VariablesUtility.replaceVariables(req, page.getKeywords()));
		ret.setDescription(VariablesUtility.replaceVariables(req, page.getDescription()));
		ret.setName(page.getName());
        ret.setLocalizedName(page.getLocalizedName());

		//populate data  to request attributes - ### start
		req.setAttribute(PageBean.PAGE_NAME_REQUEST_ATTR, page.getName());
		req.setAttribute(PageBean.PAGE_TITLE_REQUEST_ATTR, page.getTitle());
		req.setAttribute(PageBean.PAGE_DESCRIPTION_REQUEST_ATTR, page.getDescription());
		//populate data  to request attributes - ### end


		//MediaLinks
		ret.addMediaLinks(createMediaLinkBeanList(template.getMediaLinks(), req));
		ret.addMediaLinks(createMediaLinkBeanList(page.getMediaLinks(), req));

		//Scripts
		ret.addScripts(createScriptBeanList(template.getScripts(), req));
		ret.addScripts(createScriptBeanList(page.getScripts(), req));

		//attributes
		List<String> attributes = page.getAttributes();
		if (brandConfig != null) {
			attributes.addAll(brandConfig.getAttributes());
		}

		AttributeMap attributeMap = createAttributeMap(req, res, attributes);
		APICallContext.getCallContext().setAttribute(AttributeMap.PAGE_ATTRIBUTES_CALL_CONTEXT_SCOPE_NAME, attributeMap);
		ret.setAttributes(attributeMap);

		InternalResponse response = new InternalResponseContinue();

		InternalResponse call = null;
		String redirectTarget = null;
		List<BoxBean> boxes = null;

		//meta
		call = createBoxBeanList(req, res, template.getMeta());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addMetaBoxes(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		//header
		call = createBoxBeanList(req, res, template.getHeader());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addHeaderBoxes(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, page.getHeader());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addHeaderBoxes(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		//c1
		call = createBoxBeanList(req, res, template.getC1first());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn1(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, page.getC1());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn1(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, template.getC1last());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn1(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		//c2
		call = createBoxBeanList(req, res, template.getC2first());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn2(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, page.getC2());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn2(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, template.getC2last());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn2(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));


		//c3
		call = createBoxBeanList(req, res, template.getC3first());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn3(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, page.getC3());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn3(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, template.getC3last());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addColumn3(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		//footer
		call = createBoxBeanList(req, res, template.getFooter());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addFooterBoxes(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		call = createBoxBeanList(req, res, page.getFooter());
		if (!call.canContinue())
			return new InternalResponse(call.getCode());
		redirectTarget = getNewPageRedirectTargetIfApplies(call, redirectTarget);
		response = getNewPageResponse(call, response);
		boxes = ((InternalBoxBeanListResponse) call).getBeans();
		ret.addFooterBoxes(boxes);
		ret.addMediaLinks(searchMediaLinks(boxes));
		ret.addScripts(searchScripts(boxes));

		if (brandConfig != null) {
			ret.addMediaLinks(createMediaLinkBeanList(brandConfig.getBrandMediaLinkIds(), req));
		}

		return redirectTarget == null ?
				new InternalPageBeanResponse(ret) :
				new InternalPageBeanWithRedirectResponse(ret, redirectTarget);

	}

	/**
	 * Create collection of {@link MediaLinkBean}.
	 *
	 * @param mediaLinkIds links id's collection
	 * @param req		  {@link HttpServletRequest}
	 * @return {@link List<MediaLinkBean>}
	 * @throws ASGRuntimeException on errors
	 */
	private List<MediaLinkBean> createMediaLinkBeanList(List<String> mediaLinkIds, HttpServletRequest req) throws ASGRuntimeException {
		String linkPrefix = getRDPrefix();
		
		List<MediaLinkBean> ret = new ArrayList<MediaLinkBean>(mediaLinkIds.size());
		for (String id : mediaLinkIds) {			
			MediaLink item = siteDataService.getMediaLink(id);
			if (disabledByGuards(req, item))
				continue;
			
			MediaLinkBean bean = new MediaLinkBean();
			bean.setId(item.getId());
			bean.setName(item.getName());

			boolean needUseRDServlet = false;
			if (!StringUtils.isEmpty(item.getHref()) && !item.getHref().startsWith("http:") && !item.getHref().startsWith("https:") && (item.getRel() == LinkTypesUtils.stylesheet || item.getRev() == LinkTypesUtils.stylesheet))
				needUseRDServlet = true;
			
			bean.setHref(needUseRDServlet ? linkPrefix + item.getHref() : item.getHref());
			if (!StringUtils.isEmpty(item.getFile()))
				bean.setHref(getCMSFileUrl(item.getFile(), req));

			bean.setType(item.getType());
			bean.setMedia(item.getMedia() > 0 ? MediaDescUtils.getName(item.getMedia()) : MediaDescUtils.all_NAME);
			bean.setRel(item.getRel() > LinkTypesUtils.none ? LinkTypesUtils.getName(item.getRel()) : "");
			bean.setRev(item.getRev() > LinkTypesUtils.none ? LinkTypesUtils.getName(item.getRev()) : "");
			bean.setCharset(item.getCharset());
			bean.setHreflang(item.getHreflang());
			bean.setBrowserFiltering(item.getBrowserFiltering());
			bean.setOnload(item.getOnload());
			ret.add(bean);
		}
		return ret;
	}
	
	private String getRDPrefix() {
		String prefix = "";
		if (rdConfig.isUseForCMSEnabled()) {
			prefix = rdConfig.getServletMapping() + "/";
			if (rdConfig.isUseAppVersionInURL()) {
				MavenVersion mVersion = VersionUtil.getWebappVersion(getServletContext());
				if (mVersion != null)
					prefix += rdConfig.getVersionPrefix() + mVersion.getVersion() + "_" + mVersion.getFileTimestamp() + "/";
				else
					prefix += rdConfig.getVersionPrefix() + "unknown/";
			}
		}
		return prefix;
	}


	/**
	 * Search {@link MediaLinkBean} for selected {@link BoxBean} collection.
	 *
	 * @param boxBeans {@link List<BoxBean>}
	 * @return {@link List<MediaLinkBean>}
	 */
	private List<MediaLinkBean> searchMediaLinks(List<BoxBean> boxBeans) {
		List<MediaLinkBean> ret = new ArrayList<MediaLinkBean>();
		for (BoxBean box : boxBeans) {
			ret.addAll(box.getMediaLinks());
			ret.addAll(searchMediaLinks(box.getSubboxes()));
		}
		return ret;
	}

	private List<ScriptBean> createScriptBeanList(List<String> scriptIds, HttpServletRequest req) throws ASGRuntimeException {
		String linkPrefix = getRDPrefix();
		
		List<ScriptBean> ret = new ArrayList<ScriptBean>(scriptIds.size());
		for (String id : scriptIds) {
			Script item = siteDataService.getScript(id);
			if (disabledByGuards(req, item))
				continue;			
			
			ScriptBean bean = new ScriptBean(item.getId());
			bean.setName(item.getName());

			boolean needUseRDServlet = false;
			if (!StringUtils.isEmpty(item.getLink()) && !item.getLink().startsWith("http"))
				needUseRDServlet = true;
			
			bean.setLink(needUseRDServlet ? linkPrefix + item.getLink() : item.getLink());
			if (!StringUtils.isEmpty(item.getFile()))
				bean.setLink(getCMSFileUrl(item.getFile(), req));

			bean.setContent(item.getContent());
			bean.setBrowserFiltering(item.getBrowserFiltering());
			ret.add(bean);
		}
		return ret;
	}

	/**
	 * SearchScripts method.
	 *
	 * @param boxBeans {@link List<BoxBean>}
	 * @return {@link List<ScriptBean>}
	 */
	private List<ScriptBean> searchScripts(List<BoxBean> boxBeans) {
		List<ScriptBean> ret = new ArrayList<ScriptBean>();
		for (BoxBean box : boxBeans) {
			ret.addAll(box.getScripts());
			ret.addAll(searchScripts(box.getSubboxes()));
		}
		return ret;
	}

	/**
	 * Return CMS file path.
	 *
	 * @param cmsFileId string field
	 * @param req	   {@link HttpServletRequest}
	 * @return string - link to resource
	 * @throws ASGRuntimeException on errors
	 */
	private String getCMSFileUrl(String cmsFileId, HttpServletRequest req) throws ASGRuntimeException {
		FileLink f = resourceDataService.getFileLink(cmsFileId);
		return req.getContextPath() + FILE_PATH_PART + f.getFile();
	}

	/**
	 * Creates the site bean based on the template used by the page.
	 *
	 * @param template {@link PageTemplate}
	 * @param req	  {@link HttpServletRequest}
	 * @return created {@link SiteBean}
	 */
	private SiteBean createSiteBean(PageTemplate template, HttpServletRequest req) {
		SiteBean ret = new SiteBean();

		try {
			Site site = siteDataService.getSite(template.getSite());
			ret.setName(site.getName());
			ret.setSubtitle(site.getSubtitle());
			ret.setTitle(site.getTitle());
			ret.setKeywords(VariablesUtility.replaceVariables(req, site.getKeywords()));
			ret.setDescription(VariablesUtility.replaceVariables(req, site.getDescription()));
			ret.setLanguageSelector(site.getLanguageselector());
			if (site.getStartpage() != null && site.getStartpage().length() > 0)
				ret.setLinkToStartPage(webDataService.getPagex(site.getStartpage()).getName() + HTML_SUFFIX);
			if (site.getSearchpage() != null && site.getSearchpage().length() > 0)
				ret.setSearchTarget(webDataService.getPagex(site.getSearchpage()).getName() + HTML_SUFFIX);
			//populating logo!
			populateLogo(req.getContextPath() + FILE_PATH_PART, ret, site.getSiteLogo());
		} catch (Exception e) {
			LOGGER.warn("createSiteBean(" + template + ",request)", e);
		}

		return ret;
	}


	/**
	 * Simply putting path to the siteLogo image to SiteBean logo.
	 *
	 * @param pathPart context+"/file/"
	 * @param created  SiteBean instance
	 * @param logoId   imageId - itself
	 */
	private void populateLogo(String pathPart, SiteBean created, String logoId) {
		if (isNotNullOrEmpty(logoId)) {
			try {
				Image img = resourceDataService.getImage(logoId);
				if (img != null && isNotNullOrEmpty(img.getImage()))
					created.setLogo(pathPart + img.getImage());
			} catch (ASResourceDataServiceException e) {
				LOGGER.warn("SiteLogo Image with id:{" + logoId + "} does not exist!");
			}
		}
	}

	/**
	 * Allow string check. Return true when param is not  null and not empty.
	 * False in other case.
	 *
	 * @param str object to check
	 * @return boolean value
	 */
	private boolean isNotNullOrEmpty(String str) {
		return str != null && !str.isEmpty();
	}

	/**
	 * Returns the page name referenced in the request.
	 *
	 * @param req {@link HttpServletRequest}
	 * @return artifact name
	 */
	private String extractPageName(HttpServletRequest req) {
		return extractArtifactName(req);
	}

    /**
     * Trying to get page by localized name, if nothing found trying to find page by name.
     *
     * @param pageName {@link String} page name.
     * @return {@link Pagex} with selected name.
     * @throws ServletException on errors.
     */
    private Pagex resolvePageByName(String pageName) throws ServletException {
        Pagex page = getPageByLocalizedName(pageName);
        return page == null ? getPageByName(pageName) : page;
    }

	/**
	 * Returns pagex object for the given name. Returns null if nothing found.
	 *
	 * @param pageName string page name
	 * @return {@link Pagex} with selected name
	 * @throws ServletException on errors
	 */
	private Pagex getPageByName(String pageName) throws ServletException {
		try {
			return webDataService.getPagexsByProperty(PagexDocument.PROP_NAME, pageName).get(0);
		} catch (Exception e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getPageByName("+pageName+")", e);
            }
		}
		return null;
	}

    /**
     * Returns pagex object for the given localized name. Returns null if nothing found.
     *
     * @param pageName string page name
     * @return {@link Pagex} with selected name
     * @throws ServletException on errors
     */
    private Pagex getPageByLocalizedName(String pageName) throws ServletException {
        try {
            String localizedNameProperty = "localizedName_" + ContextManager.getCallContext().getCurrentLanguage();
            List<Pagex> pages = webDataService.getPagexsByProperty(localizedNameProperty, pageName);
            return pages.isEmpty() ? null : pages.get(0);
        } catch (Exception e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getPageByLocalizedName("+pageName+")", e);
            }
        }
        return null;
    }


	/**
	 * Puts all text resources into request.
	 *
	 * @param req {@link HttpServletRequest}
	 * @throws ASResourceDataServiceException on errors
	 */
	private void prepareTextResources(HttpServletRequest req) throws ASResourceDataServiceException {
		List<TextResource> resources = resourceDataService.getTextResources();
		for (TextResource r : resources)
			req.setAttribute("res." + r.getName(), VariablesUtility.replaceVariables(req, r.getValue()));
	}

	/**
	 * Prepare template localisation.
	 *
	 * @param localizationBundlesIds collection of ids
	 * @throws ASResourceDataServiceException on errors
	 * @throws ASGRuntimeException			on errors
	 */
	private void prepareTemplateLocalization(List<String> localizationBundlesIds) throws ASResourceDataServiceException, ASGRuntimeException {
		LocalizationMap.getCurrentLocalizationMap().addLocalizationBundles(LocalizationEnvironment.TEMPLATE, getLocalizationBundles(localizationBundlesIds));
	}

	/**
	 * Prepare page localization.
	 *
	 * @param localizationBundlesIds collection of ids
	 * @throws ASResourceDataServiceException on errors
	 * @throws ASGRuntimeException			on errors
	 */
	private void preparePageLocalization(List<String> localizationBundlesIds) throws ASResourceDataServiceException, ASGRuntimeException {
		LocalizationMap.getCurrentLocalizationMap().addLocalizationBundles(LocalizationEnvironment.PAGE, getLocalizationBundles(localizationBundlesIds));
	}

	/**
	 * Prepare brand localization.
	 *
	 * @param localizationBundlesIds collection of localization bundles ids
	 * @throws ASResourceDataServiceException on errors
	 * @throws ASGRuntimeException			on errors
	 */
	private void prepareBrandLocalization(List<String> localizationBundlesIds) throws ASResourceDataServiceException, ASGRuntimeException {
		LocalizationMap.getCurrentLocalizationMap().addLocalizationBundles(LocalizationEnvironment.BRAND, getLocalizationBundles(localizationBundlesIds));
	}

	/**
	 * Prepare box localization.
	 *
	 * @param box					{@link BoxBean}
	 * @param localizationBundlesIds collection of ids
	 * @throws ASResourceDataServiceException on errors
	 * @throws ASGRuntimeException			on errors
	 */
	private void prepareBoxLocalization(BoxBean box, List<String> localizationBundlesIds) throws ASResourceDataServiceException, ASGRuntimeException {
		LocalizationMap.getCurrentLocalizationMap().addLocalizationBundles(LocalizationEnvironment.BOX, getLocalizationBundles(localizationBundlesIds));
	}

	/**
	 * Return {@link List<LocalizationBundle> } with given ids.
	 *
	 * @param localizationBundlesIds collection of bundle ids
	 * @return {@link List<LocalizationBundle> }
	 * @throws ASResourceDataServiceException on errors
	 * @throws ASGRuntimeException			on errors
	 */
	private List<LocalizationBundle> getLocalizationBundles(List<String> localizationBundlesIds) throws ASResourceDataServiceException, ASGRuntimeException {
		List<LocalizationBundle> ret = new ArrayList<LocalizationBundle>();
		for (String bundleId : localizationBundlesIds)
			ret.add(resourceDataService.getLocalizationBundle(bundleId));
		return ret;
	}

	/**
	 * PageBeanCreator as BlueprintCallExecutor.
	 * Actually executes createPage on process request.
	 */
	class PageBeanCreator implements BlueprintCallExecutor {
		public Object execute(Object... parameters) throws ASGRuntimeException {
			return createPageBean(
					(HttpServletRequest) parameters[0],
					(HttpServletResponse) parameters[1],
					(Pagex) parameters[2],
					(PageTemplate) parameters[3]
			);
		}
	}

	/**
	 * BoxBeanCreator as BlueprintCallExecutor.
	 * Actually executes createBoxBean on process request.
	 */
	class BoxBeanCreator implements BlueprintCallExecutor {
		public Object execute(Object... parameters) throws ASGRuntimeException, BoxHandleException {
			return createBoxBean(
					(HttpServletRequest) parameters[0],
					(HttpServletResponse) parameters[1],
					(Box) parameters[2]
			);
		}
	}

	/**
	 * WizardExecutor as BlueprintCallExecutor.
	 * Actually executes handleWizardProcess on process request.
	 */
	class WizardExecutor implements BlueprintCallExecutor {
		@Override
		public Object execute(Object... parameters) throws Exception {
			return handleWizardProcess(
					HttpServletRequest.class.cast(parameters[0]),
					HttpServletResponse.class.cast(parameters[1]),
					WizardDef.class.cast(parameters[2]));
		}


	}

	/**
	 * Creates attribute map.
	 *
	 * @param req		  {@link HttpServletRequest}
	 * @param res		  {@link HttpServletResponse}
	 * @param attributeIds {@link List&lt;String&gt;} - ids of box or page attributes.
	 * @return {@link AttributeMap}
	 * @throws ASGRuntimeException on errors
	 */
	private AttributeMap createAttributeMap(HttpServletRequest req, HttpServletResponse res, List<String> attributeIds) throws ASGRuntimeException {
		if (attributeIds == null || attributeIds.isEmpty())
			return new AttributeMap();

		List<Attribute> attributes = createAttributes(req, res, attributeIds);

		AttributeMap ret = new AttributeMap();
		for (Attribute a : attributes) {
			ret.setAttribute(new AttributeBean(a.getKey(), a.getName(), a.getValue()));
		}

		return ret;
	}

	/**
	 * Creates a list of attributes which are linked to the current object (whenever the list with ids came from, for example a box) and which are allowed by
	 * the attached guards.
	 *
	 * @param req {@link HttpServletRequest}
	 * @param res {@link HttpServletResponse}
	 * @param ids list of ids with attributes.
	 * @return {@link List<Attribute>}
	 * @throws ASGRuntimeException on errors
	 */
	private List<Attribute> createAttributes(HttpServletRequest req, HttpServletResponse res, List<String> ids) throws ASGRuntimeException {
		ArrayList<Attribute> ret = new ArrayList<Attribute>();


		for (String id : ids) {
			boolean doBreak = false;
			Attribute a = webDataService.getAttribute(id);

			List<String> gIds = a.getGuards();
			for (String gid : gIds) {
				//first check guards
				ConditionalGuard g = null;
				try {
					g = GuardFactory.getConditionalGuard(gid);
					if (!g.isConditionFullfilled(a, req)) {
						doBreak = true;
						break;
					}
				} catch (Exception e) {
					LOGGER.warn("exception in guard: " + g + ", attr id: " + id + ", caught.", e);
				}
			}

			if (doBreak) {
				continue;
			}

			if (a.getSubattributes().size() == 0)
				ret.add(a);
			else
				ret.addAll(createAttributes(req, res, a.getSubattributes()));

		}

		return ret;
	}


	/**
	 * Tries to load a file from filesystem (in case page wasn't found) and returns true if its done successfully. This is useful for integration of
	 * simple html pages.
	 *
	 * @param req {@link HttpServletRequest}
	 * @param res {@link HttpServletResponse}
	 * @return boolean value
	 */
	private boolean fallBackToFileSystem(HttpServletRequest req, HttpServletResponse res) {
		final int pathIndex = StringUtils.isEmpty(req.getContextPath()) ? 0 : req.getContextPath().length();
		String path = getServletContext().getRealPath(req.getRequestURI().substring(pathIndex));

		File f = new File(path);
		LOGGER.debug("Loading uri: " + req.getRequestURL() + " from file " + path + ", exists: " + f.exists());
		if (!f.exists())
			return false;
		
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fIn, MY_FS_CHARSET));
			int r = -1;
			OutputStreamWriter out = new OutputStreamWriter(res.getOutputStream(), MY_FS_CHARSET);
			BufferedWriter writer = new BufferedWriter(out);
			while ((r = reader.read()) != -1) {
				//System.out.println((char)r);
				writer.write(r);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOGGER.error("fallBackToFileSystem(URI: " + req.getRequestURL() + ")", e);
			return false;
		} finally {
			if (fIn != null) {
				try {
					fIn.close();
				} catch (IOException ignored) {
				}
			}
		}


		return true;
	}


}
