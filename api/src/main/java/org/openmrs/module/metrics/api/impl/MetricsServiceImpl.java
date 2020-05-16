package org.openmrs.module.metrics.api.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.api.db.MetricsDAO;

public class MetricsServiceImpl extends BaseOpenmrsService implements MetricService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private MetricsDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(MetricsDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public MetricEvent saveMetricEvent(MetricEvent metricEvent) {
		return dao.saveMetricEvent(metricEvent);
	}
	
	@Override
	public Integer getEncounterObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange, String encounterType) {
		return dao.getEncounterObjectsByGivenDateRange(startRange, endRange, encounterType);
	}
	
	@Override
	public Integer getNewPatientsObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange) {
		return dao.getNewPatientsObjectsByGivenDateRange(startRange, endRange);
	}
}
