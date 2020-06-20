package org.openmrs.module.metrics.api.service;

import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.metrics.api.model.EventConfiguration;

public interface EventConfigurationService {
	
	EventConfiguration getEventsConfigurationByOpenMrsClass(String openMrsClass);
	
	Set<Class<? extends OpenmrsObject>> getClassesToMonitorFromConfiguration();
	
}
