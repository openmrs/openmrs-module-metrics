package org.openmrs.module.metrics.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metrics.MetricEvent;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for Metric Service API functionality
 */
@Transactional
public interface MetricService extends OpenmrsService {
	
	MetricEvent saveMetricEvent(MetricEvent metricEvent);
}
