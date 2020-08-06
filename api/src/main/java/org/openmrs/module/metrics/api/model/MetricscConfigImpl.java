package org.openmrs.module.metrics.api.model;

import java.util.Map;

import org.openmrs.module.metrics.api.model.MetricConfigMBean;

public class MetricscConfigImpl implements MetricConfigMBean {
	
	private Integer newPatientsRegistered;
	
	private Map<String, Integer> newEncounters;
	
	public MetricscConfigImpl(Integer nePatientsRegistered, Map<String, Integer> newEncounters) {
		this.newPatientsRegistered = nePatientsRegistered;
		this.newEncounters = newEncounters;
	}
	
	public Integer getNewPatientsRegistered() {
		return newPatientsRegistered;
	}
	
	public Map<String, Integer> getNewEncounters() {
		return newEncounters;
	}
}
