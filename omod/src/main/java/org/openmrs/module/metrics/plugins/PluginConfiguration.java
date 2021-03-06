package org.openmrs.module.metrics.plugins;

import static org.openmrs.module.metrics.util.MetricClientConstants.ENDPOINT_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.metrics.util.PluginHandler.parseJsonFileToEndPointConfiguration;

import org.openmrs.module.metrics.api.utils.EventsUtils;
import org.openmrs.module.metrics.model.impl.GeneralEndPointConfig;

public class PluginConfiguration {
	
	private GeneralEndPointConfig generalConfiguration = new GeneralEndPointConfig();
	
	public PluginConfiguration() {
	}
	
	public GeneralEndPointConfig parseConfigForPlugins() {
		if (EventsUtils.resourceFileExists(ENDPOINT_PATH_TO_DEFAULT_CONFIGURATION)) {
			generalConfiguration = parseJsonFileToEndPointConfiguration(ENDPOINT_PATH_TO_DEFAULT_CONFIGURATION);
		}
		return generalConfiguration;
	}
}
