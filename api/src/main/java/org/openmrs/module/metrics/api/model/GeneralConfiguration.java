package org.openmrs.module.metrics.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneralConfiguration {
	
	private List<String> eventFilterBeans;
	
	private List<EventConfiguration> eventConfigurations;
	
	public GeneralConfiguration() {
		eventFilterBeans = new ArrayList<>();
		eventConfigurations = new ArrayList<>();
	}
	
	public GeneralConfiguration(List<String> eventFilterBeans, List<EventConfiguration> eventConfigurations) {
		this.eventFilterBeans = eventFilterBeans;
		this.eventConfigurations = eventConfigurations;
	}
	
	public List<String> getEventFilterBeans() {
		return eventFilterBeans;
	}
	
	public void setEventFilterBeans(List<String> eventFilterBeans) {
		this.eventFilterBeans = eventFilterBeans;
	}
	
	public List<EventConfiguration> getEventConfigurations() {
		return eventConfigurations;
	}
	
	public void setEventConfigurations(List<EventConfiguration> eventConfigurations) {
		this.eventConfigurations = eventConfigurations;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		GeneralConfiguration that = (GeneralConfiguration) o;
		return Objects.equals(eventFilterBeans, that.eventFilterBeans)
		        && Objects.equals(eventConfigurations, that.eventConfigurations);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(eventFilterBeans, eventConfigurations);
	}
}
