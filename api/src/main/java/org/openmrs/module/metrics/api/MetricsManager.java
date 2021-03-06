package org.openmrs.module.metrics.api;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.event.Event;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.metrics.api.event.MetricsEventListener;
import org.openmrs.module.metrics.api.service.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricsManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsManager.class);

	private DaemonToken daemonToken;

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
}
