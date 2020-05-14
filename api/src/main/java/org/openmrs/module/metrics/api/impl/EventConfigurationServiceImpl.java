package org.openmrs.module.metrics.api.impl;

import static org.openmrs.module.metrics.MetricsConstants.EVENTS_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getOpenMrsClass;
import static org.openmrs.module.metrics.api.utils.EventsUtils.parseJsonFileToEventConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metrics.api.model.EventConfiguration;
import org.openmrs.module.metrics.api.model.GeneralConfiguration;
import org.openmrs.module.metrics.api.service.EventConfigurationService;
import org.openmrs.module.metrics.api.utils.EventsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventConfigurationServiceImpl extends BaseOpenmrsService implements EventConfigurationService {
	
	private List<String> feedFilters;
	
	private HashMap<String, EventConfiguration> eventConfigurationByOpenMrsClass;
	
	private Set<Class<? extends OpenmrsObject>> openMrsClassesToMonitor;
	
	public EventConfigurationServiceImpl() {
		GeneralConfiguration generalConfiguration;
		if (EventsUtils.resourceFileExists(EVENTS_PATH_TO_DEFAULT_CONFIGURATION)) {
			generalConfiguration = parseJsonFileToEventConfiguration(EVENTS_PATH_TO_DEFAULT_CONFIGURATION);
			loadEventConfigurations(generalConfiguration);
		}
	}
	
	@Override
	public EventConfiguration getEventsConfigurationByOpenMrsClass(String openMrsClass) {
		return eventConfigurationByOpenMrsClass.get(openMrsClass);
	}
	
	@Override
	public Set<Class<? extends OpenmrsObject>> getClassesToMonitorFromConfiguration() {
		return openMrsClassesToMonitor;
	}
	
	private void loadEventConfigurations(GeneralConfiguration generalConfiguration) {
		HashMap<String, EventConfiguration> byCategory = new LinkedHashMap<>();
		HashMap<String, EventConfiguration> byOpenMrsClass = new LinkedHashMap<>();
		Set<Class<? extends OpenmrsObject>> classesToMonitor =  new HashSet<>();

		for (EventConfiguration configuration : generalConfiguration.getEventConfigurations()) {
			byOpenMrsClass.put(configuration.getOpenMrsClass(), configuration);
			classesToMonitor.add(getOpenMrsClass(configuration.getOpenMrsClass()));
		}
		eventConfigurationByOpenMrsClass = byOpenMrsClass;
		feedFilters = generalConfiguration.getEventFilterBeans();
		openMrsClassesToMonitor = classesToMonitor;
	}
}
