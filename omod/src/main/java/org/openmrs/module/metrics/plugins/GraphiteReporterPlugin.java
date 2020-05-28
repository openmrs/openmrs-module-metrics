package org.openmrs.module.metrics.plugins;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.module.metrics.MetricConfig;
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.openmrs.module.metrics.model.impl.GeneralEndPointConfig;
import org.openmrs.module.metrics.model.impl.GraphiteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphiteReporterPlugin implements GlobalPropertyListener {
	
	private static final Logger log = LoggerFactory.getLogger(GraphiteReporterPlugin.class);
	
	@Autowired
	private JmxReportBuilder jmxReportBuilder;
	
	@Autowired
	private PluginConfiguration pluginConfiguration;
	
	public GraphiteReporterPlugin() {
	}
	
	@Override
	public boolean supportsPropertyName(String propertyName) {
		return MetricConfig.METRICS_GRAPHITE_ENABLED.equals(propertyName);
	}
	
	@Override
	public void globalPropertyChanged(GlobalProperty newValue) {
		log.trace("Notified of change to property {}", MetricConfig.METRICS_GRAPHITE_ENABLED);
		
		if (StringUtils.isNotBlank((String) newValue.getValue())) {
			//for now just putting this here wanted to get opinion of ibachler before moving with the pluging.
			MetricRegistry metricRegistry = jmxReportBuilder.initializeMetricRegistry();
			GeneralEndPointConfig generalConfiguration = pluginConfiguration.parseConfigForPlugins();
			if (generalConfiguration != null && generalConfiguration.getGraphiteConfigurations() != null) {
				configureReporters(metricRegistry, generalConfiguration.getGraphiteConfigurations());
			}
		} else {
			stopReporters();
		}
	}
	
	@Override
	public void globalPropertyDeleted(String propertyName) {
		stopReporters();
	}
	
	List<GraphiteReporter> graphiteReporters;
	
	public void configureReporters(MetricRegistry metricRegistry, List<GraphiteConfiguration> graphiteConfigurationList) {
		for (GraphiteConfiguration graphiteConfiguration : graphiteConfigurationList) {
			final Graphite graphite = new Graphite(new InetSocketAddress(graphiteConfiguration.getGrpahiteHost(),
			        graphiteConfiguration.getGraphitePort()));
			final GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
			        .prefixedWith(graphiteConfiguration.getGraphitePrefix()).convertRatesTo(TimeUnit.SECONDS)
			        .convertDurationsTo(TimeUnit.MILLISECONDS).filter(MetricFilter.ALL).build(graphite);
			reporter.start(graphiteConfiguration.getTimeForPoll(), TimeUnit.MILLISECONDS);
			graphiteReporters.add(reporter);
		}
	}
	
	public void stopReporters() {
		for (GraphiteReporter graphiteReporter : graphiteReporters) {
			graphiteReporter.stop();
		}
	}
}
