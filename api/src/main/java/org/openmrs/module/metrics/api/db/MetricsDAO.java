package org.openmrs.module.metrics.api.db;

import java.time.LocalDateTime;
import java.util.Map;
import org.openmrs.module.metrics.MetricEvent;

public interface MetricsDAO {
	
	MetricEvent getMetricEventByUuid(String uuid);
	
	MetricEvent saveMetricEvent(MetricEvent metricEvent);
	
	Integer getEncounterObjectsByGivenDateRangeAndType(LocalDateTime startRange, LocalDateTime endRange, String encounterType);
	
	Integer getNewPatientsObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange);
	
	Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange);
}
