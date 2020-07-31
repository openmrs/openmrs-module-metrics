package org.openmrs.module.metrics.api.service;

import java.util.Date;
import java.util.Map;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metrics.MetricEvent;

/**
 * Service interface for Metric Service API functionality
 */
public interface MetricService extends OpenmrsService {
	
	MetricEvent saveMetricEvent(MetricEvent metricEvent);
	
	Integer getEncounterObjectsByGivenDateRange(Date startRange, Date endRange, String encounterType);
	
	Integer getNewPatientsObjectsByGivenDateRange(Date startRange, Date endRange);
	
	Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(Date startRange, Date endRange);
}
