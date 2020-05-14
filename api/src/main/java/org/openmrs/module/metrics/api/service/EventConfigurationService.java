package org.openmrs.module.metrics.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metrics.api.model.EventConfiguration;

public interface EventConfigurationService extends OpenmrsService {
	
	EventConfiguration getEventsConfigurationByOpenMrsClass(String openMrsClass);
}
