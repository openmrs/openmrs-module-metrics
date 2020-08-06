package org.openmrs.module.metrics.plugins;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.module.metrics.MetricConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrometheusReporterPlugin implements GlobalPropertyListener {
	
	private static final Logger log = LoggerFactory.getLogger(PrometheusReporterPlugin.class);
	
	public PrometheusReporterPlugin() {
	}
	
	@Override
	public boolean supportsPropertyName(String propertyName) {
		return MetricConfig.METRICS_PROMETHEUS_ENABLED.equals(propertyName);
	}
	
	@Override
	public void globalPropertyChanged(GlobalProperty newValue) {
		log.trace("Notified of change to property {}", MetricConfig.METRICS_PROMETHEUS_ENABLED);
		
		if (StringUtils.isNotBlank((String) newValue.getValue())) {
			MetricConfig.IS_PROMETHEUS_ENABLED = true;
		} else {
			stopReporters();
		}
	}
	
	@Override
	public void globalPropertyDeleted(String propertyName) {
		stopReporters();
	}
	
	public void stopReporters() {
		MetricConfig.IS_PROMETHEUS_ENABLED = false;
	}
}
