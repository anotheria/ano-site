package net.anotheria.anosite.content.variables;

import junit.framework.Assert;
import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.anoprise.metafactory.Extension;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asresourcedata.data.TextResource;
import net.anotheria.anosite.gen.asresourcedata.data.TextResourceDocument;
import net.anotheria.anosite.gen.asresourcedata.service.ASResourceDataServiceException;
import net.anotheria.anosite.gen.asresourcedata.service.IASResourceDataService;
import net.anotheria.anosite.gen.asresourcedata.service.fixture.ASResourceDataServiceFixtureFactory;
import net.anotheria.anosite.gen.util.AnositeCallContext;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;


/**
 * Junit for TExtResourceProcessor.
 *
 * @author: h3llka
 */
public class TextResourceProcessorTestCase {
	private static TextResourceProcessor testProcessor;

	private static IASResourceDataService dataService;

	private static TextResource testResource;

	private static String testLanguage = "DE";
	public static final String TEST_RESOURCE_NAME = "testResource";
	public static final String TEST_RESOURCE_VALUE = "testValue!!!";
	private static HttpServletRequest req;

	@BeforeClass
	public static void setUp() throws MetaFactoryException, ASResourceDataServiceException {
		MetaFactory.addFactoryClass(IASResourceDataService.class, Extension.CMS, ASResourceDataServiceFixtureFactory.class);
		MetaFactory.addFactoryClass(IASResourceDataService.class, Extension.LOCAL, ASResourceDataServiceFixtureFactory.class);
		MetaFactory.addFactoryClass(IASResourceDataService.class, Extension.NONE, ASResourceDataServiceFixtureFactory.class);
		ContextManager.setCallContext(new AnositeCallContext());
		ContextManager.getCallContext().setCurrentLanguage("DE");
		dataService = MetaFactory.get(IASResourceDataService.class);
		testResource = new TextResourceDocument("1");
		testResource.setName(TEST_RESOURCE_NAME);
		testResource.setValue(TEST_RESOURCE_VALUE);
		dataService.createTextResource(testResource);

		testProcessor = new TextResourceProcessor();
		req = new TestHttpServletRequest();

	}

	@Test
	public void testProcessor() {

		String defaultVal = "default";
		String result = testProcessor.replace("", "", defaultVal, req);
		Assert.assertNotNull("Should not be null", result);

		result = testProcessor.replace(TextResourceProcessor.PREFIX, "", defaultVal, req);
		Assert.assertNotNull("Should not be null", result);

		result = testProcessor.replace(TextResourceProcessor.PREFIX, "123", defaultVal, req);
		Assert.assertNotNull("Should not be null", result);

		result = testProcessor.replace(TextResourceProcessor.PREFIX, "not Exist!", defaultVal, req);
		Assert.assertNotNull("Should not be null", result);

		result = testProcessor.replace(TextResourceProcessor.PREFIX, TEST_RESOURCE_NAME, defaultVal, req);
		Assert.assertEquals("Should be equals to default", TEST_RESOURCE_VALUE, result);
	}

	private static class TestHttpServletRequest implements HttpServletRequest{

		public String getAuthType() {
			return null;
		}

		public Cookie[] getCookies() {
			return new Cookie[0];
		}

		public long getDateHeader(String s) {
			return 0;
		}

		public String getHeader(String s) {
			return null;
		}

		public Enumeration getHeaders(String s) {
			return null;
		}

		public Enumeration getHeaderNames() {
			return null;
		}

		public int getIntHeader(String s) {
			return 0;
		}

		public String getMethod() {
			return null;
		}

		public String getPathInfo() {
			return null;
		}

		public String getPathTranslated() {
			return null;
		}

		public String getContextPath() {
			return null;
		}

		public String getQueryString() {
			return null;
		}

		public String getRemoteUser() {
			return null;
		}

		public boolean isUserInRole(String s) {
			return false;
		}

		public Principal getUserPrincipal() {
			return null;
		}

		public String getRequestedSessionId() {
			return null;
		}

		public String getRequestURI() {
			return "TestRequestURI";
		}

		public StringBuffer getRequestURL() {
			return null;
		}

		public String getServletPath() {
			return null;
		}

		public HttpSession getSession(boolean b) {
			return null;
		}

		public HttpSession getSession() {
			return null;
		}

		@Override
		public String changeSessionId() {
			return null;
		}

		public boolean isRequestedSessionIdValid() {
			return false;
		}

		public boolean isRequestedSessionIdFromCookie() {
			return false;
		}

		public boolean isRequestedSessionIdFromURL() {
			return false;
		}

		/**
		 * @deprecated
		 */
		public boolean isRequestedSessionIdFromUrl() {
			return false;
		}

		@Override
		public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
			return false;
		}

		@Override
		public void login(String s, String s2) throws ServletException {

		}

		@Override
		public void logout() throws ServletException {

		}

		@Override
		public Collection<Part> getParts() throws IOException, ServletException {
			return null;
		}

		@Override
		public Part getPart(String s) throws IOException, ServletException {
			return null;
		}

		@Override
		public <T extends HttpUpgradeHandler> T upgrade(Class<T> tClass) throws IOException, ServletException {
			return null;
		}

		public Object getAttribute(String s) {
			return null;
		}

		public Enumeration getAttributeNames() {
			return null;
		}

		public String getCharacterEncoding() {
			return null;
		}

		public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
		}

		public int getContentLength() {
			return 0;
		}

		@Override
		public long getContentLengthLong() {
			return 0;
		}

		public String getContentType() {
			return null;
		}

		public ServletInputStream getInputStream() throws IOException {
			return null;
		}

		public String getParameter(String s) {
			return null;
		}

		public Enumeration getParameterNames() {
			return null;
		}

		public String[] getParameterValues(String s) {
			return new String[0];
		}

		public Map getParameterMap() {
			return null;
		}

		public String getProtocol() {
			return null;
		}

		public String getScheme() {
			return null;
		}

		public String getServerName() {
			return "TsetServerName";
		}

		public int getServerPort() {
			return 0;
		}

		public BufferedReader getReader() throws IOException {
			return null;
		}

		public String getRemoteAddr() {
			return null;
		}

		public String getRemoteHost() {
			return null;
		}

		public void setAttribute(String s, Object o) {
		}

		public void removeAttribute(String s) {
		}

		public Locale getLocale() {
			return null;
		}

		public Enumeration getLocales() {
			return null;
		}

		public boolean isSecure() {
			return false;
		}

		public RequestDispatcher getRequestDispatcher(String s) {
			return null;
		}

		/**
		 * @deprecated
		 */
		public String getRealPath(String s) {
			return null;
		}

		public int getRemotePort() {
			return 0;
		}

		public String getLocalName() {
			return null;
		}

		public String getLocalAddr() {
			return null;
		}

		public int getLocalPort() {
			return 0;
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

		@Override
		public AsyncContext startAsync() throws IllegalStateException {
			return null;
		}

		@Override
		public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
			return null;
		}

		@Override
		public boolean isAsyncStarted() {
			return false;
		}

		@Override
		public boolean isAsyncSupported() {
			return false;
		}

		@Override
		public AsyncContext getAsyncContext() {
			return null;
		}

		@Override
		public DispatcherType getDispatcherType() {
			return null;
		}


	}
}
