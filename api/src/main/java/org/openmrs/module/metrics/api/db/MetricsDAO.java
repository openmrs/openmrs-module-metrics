package org.openmrs.module.metrics.api.db;

import java.util.Date;
import java.util.Map;
import org.openmrs.module.metrics.MetricEvent;

public interface MetricsDAO {
	
	MetricEvent getMetricEventByUuid(String uuid);
	
	MetricEvent saveMetricEvent(MetricEvent metricEvent);
	
	Integer getEncounterObjectsByGivenDateRangeAndType(Date startRange, Date endRange, String encounterType);
	
	Integer getNewPatientsObjectsByGivenDateRange(Date startRange, Date endRange);
	
	Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(Date startRange, Date endRange);
}
