package net.anotheria.anosite.api.configuration;

import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anosite.shared.AnositeConfig;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link SystemConfigurationAPI} implementation
 *
 */
public class SystemConfigurationAPIImpl extends AbstractAPIImpl implements SystemConfigurationAPI {


	@Override
	public void deInit() {
		super.deInit();
	}

	@Override
	public void init() throws APIInitException {
		super.init();
	}

	@Override
	public String getCurrentSystem() {
		Environment environment = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		Environment resultEnviroment = environment;
		while(environment.isReduceable() && (environment = environment.reduce()) != GlobalEnvironment.INSTANCE) {
			resultEnviroment = environment;
		}
		return resultEnviroment.expandedStringForm().toUpperCase();
	}

	@Override
	public String getCurrentSystemExpanded() {
		return ConfigurationManager.INSTANCE.getDefaultEnvironment().expandedStringForm().toUpperCase();
	}

	@Override
	public List<String> getAvailableSystems() {
		String[] systemsList = AnositeConfig.getInstance().getSystemsList();
		if (systemsList != null) {
			return Arrays.asList(systemsList);
		}

		return new ArrayList<>();
	}

	@Override
	public String getCurrentApplication() {
		return AnositeConfig.getInstance().getApp();
	}
}
