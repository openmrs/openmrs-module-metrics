package org.openmrs.module.metrics.model;

import java.util.Map;

public interface MetricConfigMBean {
	
	Integer getNePatientsRegistered();
	
	public Map<String, Integer> getNewEncounters();
}
