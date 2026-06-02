package net.anotheria.anosite.shared;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe
public class AnositeConfig{
	private static AnositeConfig instance = new AnositeConfig();
	
	@Configure private boolean enforceHttps = true;
	@Configure private boolean enforceHttp = true;
	@Configure private boolean verbose = false;
	@Configure private boolean httpsOnly = false;
	/**
	 * Application name.
	 */
	@Configure private String app = "";
	@Configure private String[] systemsList = null;
	
	public static AnositeConfig getInstance(){ return instance; }
		
	private AnositeConfig(){
		ConfigurationManager.INSTANCE.configure(this);
	}
	
	public boolean enforceHttps(){ return enforceHttps; }
	
	public void setEnforceHttps(boolean aValue){
		enforceHttps = aValue;
	}
	
	public boolean verbose() { return verbose; }
	
	public void setVerbose(boolean aValue){
		verbose = aValue;
	}
	
	public void setHttpsOnly(boolean aValue){
		httpsOnly = aValue;
	}
	
	public boolean httpsOnly(){ return httpsOnly; }

	/**
	 * If true and a user is on a https page, but the page doesn't require https, he will be redirected to http.
	 * @return true if http is enforced.
	 */
	public boolean enforceHttp(){ return enforceHttp; }
	
	public void setEnforceHttp(boolean aValue){
		enforceHttp = aValue;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String[] getSystemsList() {
		return systemsList;
	}

	public void setSystemsList(String[] systemList) {
		this.systemsList = systemList;
	}
}
