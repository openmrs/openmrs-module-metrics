package org.openmrs.module.metrics.api.impl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.api.db.MetricsDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MetricsServiceImpl extends BaseOpenmrsService implements MetricService {
	
	private MetricsDAO dao;
	
	@Override
	public MetricEvent saveMetricEvent(MetricEvent metricEvent) {
		return dao.saveMetricEvent(metricEvent);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer getEncounterObjectsByGivenDateRange(Date startRange, Date endRange, String encounterType) {
		return dao.getEncounterObjectsByGivenDateRangeAndType(startRange, endRange, encounterType);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer getNewPatientsObjectsByGivenDateRange(Date startRange, Date endRange) {
		return dao.getNewPatientsObjectsByGivenDateRange(startRange, endRange);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(Date startRange, Date endRange) {
		return dao.getEncounterObjectTypesCountByGivenDateRange(startRange, endRange);
	}
	
	public void setMetricsDAO(MetricsDAO metricDAO) {
		this.dao = metricDAO;
	}
}
