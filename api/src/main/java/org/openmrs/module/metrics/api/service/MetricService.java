package org.openmrs.module.metrics.api.service;

import java.util.Date;
import java.util.Map;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.EventAction;

/**
 * Service interface for Metric Service API functionality
 */
public interface MetricService extends OpenmrsService {

	MetricEvent getMetricEventByUuid(String uuid);

	MetricEvent saveMetricEvent(MetricEvent metricEvent);

	int getEncounterObjectsByGivenDateRange(Date startRange, Date endRange, String encounterType);

	int getNewObjectsByGivenDateRangeAndCategory(Date startRange, Date endRange, String category, EventAction action);

	Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(Date startRange, Date endRange);

	int fetchTotalOpenMRSObjectCountTodayByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action);

	int fetchTotalOpenMRSObjectCountThisWeekByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action);

	int fetchTotalOpenMRSObjectCountLastWeekByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action);

	int fetchTotalOpenMRSObjectCountLastMonthByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action);

	int fetchTotalOpenMRSObjectCountThisMonthByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action);

	int fetchTotalOpenMRSObjectCountThisYearByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action);

	int fetchTotalOpenMRSObjectCountLastYearByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action);
}
