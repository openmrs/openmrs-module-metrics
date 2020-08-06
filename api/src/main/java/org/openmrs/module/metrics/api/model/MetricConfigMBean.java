package org.openmrs.module.metrics.api.model;

import java.util.Map;

public interface MetricConfigMBean {
	
	Integer getNewPatientsRegistered();
	
	public Map<String, Integer> getNewEncounters();
}
