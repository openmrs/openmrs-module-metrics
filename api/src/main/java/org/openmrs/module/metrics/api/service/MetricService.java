package org.openmrs.module.metrics.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metrics.MetricEvent;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for Metric Service API functionality
 */
public interface MetricService extends OpenmrsService {
	
	MetricEvent saveMetricEvent(MetricEvent metricEvent);
	
	Integer getEncounterObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange, String encounterType);
	
	Integer getNewPatientsObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange);
	
	Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange);
}
