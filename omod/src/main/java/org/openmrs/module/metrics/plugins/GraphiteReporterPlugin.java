package org.openmrs.module.metrics.plugins;

import static org.openmrs.module.metrics.util.MetricClientConstants.ENDPOINT_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.metrics.util.MetricHandler.parseJsonFileToEndPointConfiguration;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.openmrs.module.metrics.model.impl.GraphiteConfiguration;
import org.springframework.stereotype.Component;

@Component
public class GraphiteReporterPlugin {
	
	public GraphiteReporterPlugin() {
		
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
