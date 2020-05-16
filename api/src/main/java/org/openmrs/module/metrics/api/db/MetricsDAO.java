package org.openmrs.module.metrics.api.db;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.module.metrics.MetricEvent;

public interface MetricsDAO {
	
	MetricEvent saveMetricEvent(MetricEvent metricEvent);
	
	Integer getEncounterObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange, String encounterType);
	
	Integer getNewPatientsObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange);
}
