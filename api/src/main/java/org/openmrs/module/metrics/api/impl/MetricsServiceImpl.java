package org.openmrs.module.metrics.api.impl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.EventAction;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.api.db.MetricsDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MetricsServiceImpl extends BaseOpenmrsService implements MetricService {

	private MetricsDAO dao;

	@Override
	public MetricEvent getMetricEventByUuid(String uuid) {
		return dao.getMetricEventByUuid(uuid);
	}

	@Override
	public MetricEvent saveMetricEvent(MetricEvent metricEvent) {
		return dao.saveMetricEvent(metricEvent);
	}

	@Override
	@Transactional(readOnly = true)
	public int getEncounterObjectsByGivenDateRange(Date startRange, Date endRange, String encounterType) {
		return dao.getEncounterObjectsByGivenDateRangeAndType(startRange, endRange, encounterType);
	}

	@Override
	@Transactional(readOnly = true)
	public int getNewObjectsByGivenDateRangeAndCategory(Date startRange, Date endRange, String category, EventAction action) {
		return dao.getNewObjectsByGivenDateRangeAndCategory(startRange, endRange, category, action);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(Date startRange, Date endRange) {
		return dao.getEncounterObjectTypesCountByGivenDateRange(startRange, endRange);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountTodayByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return dao.fetchTotalOpenMRSObjectCountTodayByEventAction(clazz, action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountThisWeekByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return dao.fetchTotalOpenMRSObjectCountThisWeekByEventAction(clazz, action);

	}

	@Override
	public int fetchTotalOpenMRSObjectCountLastWeekByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return dao.fetchTotalOpenMRSObjectCountLastWeekByEventAction(clazz, action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountLastMonthByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return dao.fetchTotalOpenMRSObjectCountLastMonthByEventAction(clazz, action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountThisMonthByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return dao.fetchTotalOpenMRSObjectCountThisMonthByEventAction(clazz, action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountThisYearByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return dao.fetchTotalOpenMRSObjectCountThisYearByEventAction(clazz, action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountLastYearByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return dao.fetchTotalOpenMRSObjectCountLastYearByEventAction(clazz, action);
	}

	public void setMetricsDAO(MetricsDAO metricDAO) {
		this.dao = metricDAO;
	}
}
