package org.openmrs.module.metrics.api.db;

import org.openmrs.module.metrics.MetricEvent;

public interface MetricsDAO {
	
	MetricEvent saveMetricEvent(MetricEvent metricEvent);
}
