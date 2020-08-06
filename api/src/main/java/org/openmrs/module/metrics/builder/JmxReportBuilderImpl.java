package org.openmrs.module.metrics.builder;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import org.openmrs.module.metrics.api.model.MetricscConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JmxReportBuilderImpl {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JmxReportBuilderImpl.class);
	
	private MetricRegistry metricRegistry;
	
	public MetricRegistry initializeMetricRegistry() {
		return metricRegistry;
	}
	
	@Autowired
	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}
	
	public MetricRegistry registerNewMetric(MetricRegistry metricRegistry, Object metricValue, String metricName) {
		LOGGER.debug("Registering metric '{}' to the metric registry", metricName);
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
