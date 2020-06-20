package org.openmrs.module.metrics;

import org.springframework.stereotype.Component;

public class MetricConfig {
	
	public static final String METRICS_GRAPHITE_ENABLED = "metric.grpahiteenabled";
	
	public static final String METRICS_PROMETHEUS_ENABLED = "metric.prometheusenabled";
	
	public static Boolean IS_PROMETHEUS_ENABLED = false;
}
