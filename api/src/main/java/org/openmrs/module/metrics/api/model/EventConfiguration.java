package org.openmrs.module.metrics.api.model;

import java.util.LinkedHashMap;
import java.util.Objects;

public class EventConfiguration {
	
	private String openMrsClass;
	
	private boolean enabled;
	
	private String title;
	
	private String category;
	
	private LinkedHashMap<String, String> linkTemplates;
	
	public EventConfiguration() {
	}
	
	public EventConfiguration(String openMrsClass, String title, String category,
	    LinkedHashMap<String, String> linkTemplates, String feedWriter) {
		this.openMrsClass = openMrsClass;
		this.enabled = false;
		this.title = title;
		this.category = category;
		this.linkTemplates = linkTemplates;
	}
	
	public String getOpenMrsClass() {
		return openMrsClass;
	}
	
	public void setOpenMrsClass(String openMrsClass) {
		this.openMrsClass = openMrsClass;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public LinkedHashMap<String, String> getLinkTemplates() {
		return linkTemplates;
	}
	
	public void setLinkTemplates(LinkedHashMap<String, String> linkTemplates) {
		this.linkTemplates = linkTemplates;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		EventConfiguration eventConfiguration = (EventConfiguration) o;
		return Objects.equals(openMrsClass, eventConfiguration.openMrsClass)
		        && Objects.equals(enabled, eventConfiguration.enabled) && Objects.equals(title, eventConfiguration.title)
		        && Objects.equals(category, eventConfiguration.category)
		        && Objects.equals(linkTemplates, eventConfiguration.linkTemplates);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(openMrsClass, enabled, title, category, linkTemplates);
	}
}
