package org.openmrs.module.metrics.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDateTime;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.OpenmrsObject;
import org.openmrs.event.Event;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.EventAction;
import org.openmrs.module.metrics.api.event.MetricsEventListener;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.model.EventConfiguration;
import org.openmrs.module.metrics.api.service.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricsManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsManager.class);

	private DaemonToken daemonToken;

	private static final String UUID_PATTERN = "{uuid}";

	@Autowired
	private MetricService metricService;
	
	private Set<Class<? extends OpenmrsObject>> classesToMonitor = new HashSet<>();
	
	private final MetricsEventListener eventListener = new MetricsEventListener();
	
	private boolean hasStarted = false;

	public void setDaemonToken(DaemonToken daemonToken) {
		this.daemonToken = daemonToken;
	}
	
	public void start() {
		if (daemonToken == null) {
			throw new IllegalStateException("Cannot start MetricsManager without a valid DaemonToken");
		}

		eventListener.setToken(daemonToken);
		
		for (Class<? extends OpenmrsObject> classToMonitor : classesToMonitor) {
			Event.subscribe(classToMonitor, null, eventListener);
		}
		
		hasStarted = true;
	}
	
	public void stop() {
		for (Class<? extends OpenmrsObject> classToMonitor : classesToMonitor) {
			Event.unsubscribe(classToMonitor, null, eventListener);
		}
		
		hasStarted = false;
	}
	
	public void addClassToMonitor(Class<? extends OpenmrsObject> classToMonitor) {
		if (hasStarted) {
			throw new IllegalStateException("cannot add a class to monitor while already running");
		}
		
		classesToMonitor.add(classToMonitor);
	}
	
	public void setClassesToMonitor(Set<Class<? extends OpenmrsObject>> setOfClassesToMonitor) {
		if (hasStarted) {
			throw new IllegalStateException("cannot change classes to monitor while already running");
		}
		
		if (classesToMonitor.isEmpty()) {
			classesToMonitor = setOfClassesToMonitor;
		} else {
			classesToMonitor.addAll(setOfClassesToMonitor);
		}
	}

	public void buildEventObject(OpenmrsObject openmrsObject, EventAction eventAction, EventConfiguration eventConfiguration){
		if (!eventConfiguration.isEnabled()) {
			LOGGER.debug("Skipped writing '{}' Event to the database because "
							+ "the synchronization for this object is disabled in the configuration",
					openmrsObject.getClass().getName());
			return;
		}

		final MetricEvent event = new MetricEvent(
				eventConfiguration.getTitle(),
				LocalDateTime.now(),
				null,
				getEventContent(openmrsObject, eventConfiguration),
				eventConfiguration.getCategory(),
				eventAction.name(),
				LocalDateTime.now() //for now added current time stamp have to debug and see how the event object looks alike
		);

		metricService.saveMetricEvent(event);

		LOGGER.info("An event from class {} has been saved in Events DB", openmrsObject.getClass().getName());
	}

	private String getEventContent(OpenmrsObject openmrsObject, EventConfiguration eventConfiguration) {

		String uuid = openmrsObject.getUuid();
		Map<String, String> urls = new HashMap<>();
		for (Map.Entry<String, String> entry : eventConfiguration.getLinkTemplates().entrySet()) {
			urls.put(entry.getKey(), entry.getValue().replace(UUID_PATTERN, uuid));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(urls);
		} catch (IOException e) {
			throw new MetricsException("There is a problem with serialize resource links to Events content");
		}
	}
}
