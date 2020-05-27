package org.openmrs.module.metrics.plugins;

import static org.openmrs.module.metrics.util.MetricClientConstants.ENDPOINT_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.metrics.util.MetricHandler.parseJsonFileToEndPointConfiguration;

import java.util.HashMap;

import com.codahale.metrics.MetricRegistry;
import org.openmrs.module.metrics.api.utils.EventsUtils;
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.openmrs.module.metrics.model.impl.GeneralEndPointConfig;
import org.openmrs.module.metrics.model.impl.GraphiteConfiguration;
import org.openmrs.module.metrics.util.MetricHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class PluginConfiguration {
	
	@Autowired
	private GraphiteReporterPlugin graphiteReporterPlugin;
	
	private GeneralEndPointConfig generalConfiguration;
	
	@Autowired
	private MetricHandler metricHandler;
	
	@Autowired
	private JmxReportBuilder jmxReportBuilder;
	
	public PluginConfiguration() {
	}
	
	public void startReportPlugins() {
		if (EventsUtils.resourceFileExists(ENDPOINT_PATH_TO_DEFAULT_CONFIGURATION)) {
			generalConfiguration = parseJsonFileToEndPointConfiguration(ENDPOINT_PATH_TO_DEFAULT_CONFIGURATION);
		}
		enablePlugins(generalConfiguration);
	}
	
	public void enablePlugins(GeneralEndPointConfig generalConfiguration) {
		
		//for now just putting this here wanted to get opinion of ibachler before moving with the pluging.
		MetricRegistry metricRegistry = jmxReportBuilder.initializeMetricRegistry();
		
		//when new plugins added we can add them here.
		if (generalConfiguration.getGraphiteConfigurations() != null) {
			graphiteReporterPlugin.configureReporters(metricRegistry, generalConfiguration.getGraphiteConfigurations());
		}
	}
	
	public void disablePlugin() {
		graphiteReporterPlugin.stopReporters();
	}
}
