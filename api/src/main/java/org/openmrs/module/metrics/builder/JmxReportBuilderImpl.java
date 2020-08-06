package org.openmrs.module.metrics.builder;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.metrics.api.model.MetricscConfigImpl;
import org.openmrs.module.metrics.api.utils.EventsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxReportBuilderImpl {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JmxReportBuilderImpl.class);

	private MetricRegistry metricRegistry;
	
	public MetricRegistry initializeMetricRegistry() {
		return metricRegistry;
	}
	
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
	
	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}
	
	public MetricRegistry registerNewMetric(MetricRegistry metricRegistry, Object metricValue, String metricName) {
		metricRegistry.register(metricRegistry.name(MetricscConfigImpl.class, metricName),
		    new CachedGauge(15, TimeUnit.MINUTES) {
			    @Override
			    protected Object loadValue() {
				    return metricValue;
			    }
		    });
		return metricRegistry;
	}
}
