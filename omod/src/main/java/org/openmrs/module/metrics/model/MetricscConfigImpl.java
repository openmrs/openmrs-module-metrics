package org.openmrs.module.metrics.model;

import java.util.Map;

public class MetricscConfigImpl implements MetricConfigMBean {
	
	private Integer nePatientsRegistered;
	
	private Map<String, Integer> newEncounters;
	
	public MetricscConfigImpl(Integer nePatientsRegistered, Map<String, Integer> newEncounters) {
		this.nePatientsRegistered = nePatientsRegistered;
		this.newEncounters = newEncounters;
	}
	
	public Integer getNePatientsRegistered() {
		return nePatientsRegistered;
	}
	
	public Map<String, Integer> getNewEncounters() {
		return newEncounters;
	}
}
