package net.anotheria.anosite.shared;

import net.anotheria.anosite.transfer.TransferTargetGroup;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ConfigureMe
public class AnositeConfig{
	private static final Logger LOGGER = LoggerFactory.getLogger(AnositeConfig.class);

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
	/**
	 * Whether documents may be transferred from this instance. Off by default: an instance only publishes into
	 * other systems if it was explicitly set up to, which is a property of the instance and not of the
	 * environment it happens to be configured for. A standalone cms is the instance that has this on; the
	 * systems it publishes to have it off, even though they run the same war.
	 */
	@Configure private boolean transferEnabled = false;
	/**
	 * Where documents can be transferred to, grouped. Editors pick a group and the transfer reaches every
	 * target in it. Empty means nowhere, which disables transfer just as effectively as {@link #transferEnabled}.
	 */
	@Configure private TransferTargetGroup[] transferTargetGroups = new TransferTargetGroup[0];
	
	public static AnositeConfig getInstance(){ return instance; }
		
	private AnositeConfig(){
		try{
			ConfigurationManager.INSTANCE.configure(this);
		}catch(IllegalArgumentException e){
			//an installation without an anositeconfig.json runs on the defaults below, which is what it
			//always meant to do. Throwing here used to take the whole class down with an
			//ExceptionInInitializerError, and since the auto transfer asks this config on every saved
			//document, that would turn a missing file into an error per save.
			LOGGER.warn("No anositeconfig found, running with defaults [" + e.getMessage() + "]");
		}
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

	/**
	 * True if this instance is allowed to transfer documents to other instances.
	 *
	 * @return true if transfer is enabled here.
	 */
	public boolean isTransferEnabled() {
		return transferEnabled;
	}

	public void setTransferEnabled(boolean aTransferEnabled) {
		this.transferEnabled = aTransferEnabled;
	}

	public TransferTargetGroup[] getTransferTargetGroups() {
		return transferTargetGroups;
	}

	public void setTransferTargetGroups(TransferTargetGroup[] aTransferTargetGroups) {
		this.transferTargetGroups = aTransferTargetGroups == null ? new TransferTargetGroup[0] : aTransferTargetGroups;
	}

	/**
	 * The groups that are actually usable, in configuration order. A group without a name or without a single
	 * target with a url is dropped instead of being offered to editors as something that cannot work.
	 *
	 * @return usable transfer target groups, empty if transfer is disabled or nothing is configured.
	 */
	public List<TransferTargetGroup> getUsableTransferTargetGroups() {
		List<TransferTargetGroup> usable = new ArrayList<>();
		if (!transferEnabled)
			return usable;

		for (TransferTargetGroup group : transferTargetGroups)
			if (group != null && group.isValid())
				usable.add(group);

		return usable;
	}

	/**
	 * The groups every editor change is published into by itself, among the usable ones.
	 *
	 * <p>Empty unless a group says {@code autoTransfer: true}, and empty whenever transfer is off here - the
	 * auto transfer is the ordinary transfer without an editor clicking it, not a second mechanism with its
	 * own switch.
	 *
	 * @return usable groups with auto transfer enabled, in configuration order.
	 */
	public List<TransferTargetGroup> getAutoTransferTargetGroups() {
		List<TransferTargetGroup> auto = new ArrayList<>();
		for (TransferTargetGroup group : getUsableTransferTargetGroups())
			if (group.isAutoTransfer())
				auto.add(group);

		return auto;
	}

	/**
	 * Looks a group up by name, among the usable ones.
	 *
	 * @param name name of the group as configured.
	 * @return the group, or null if there is no usable group under that name.
	 */
	public TransferTargetGroup getTransferTargetGroup(String name) {
		if (name == null)
			return null;

		for (TransferTargetGroup group : getUsableTransferTargetGroups())
			if (name.equals(group.getName()))
				return group;

		return null;
	}
}
