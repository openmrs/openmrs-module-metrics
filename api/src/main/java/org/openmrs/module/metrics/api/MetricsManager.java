package org.openmrs.module.metrics.api;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.Auditable;
import org.openmrs.OpenmrsObject;
import org.openmrs.event.Event;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.metrics.api.event.MetricsEventListener;

public class MetricsManager {
	
	private DaemonToken token;
	
	public MetricsManager(DaemonToken token) {
		this.token = token;
	}
	
	private Set<Class<? extends OpenmrsObject>> classesToMonitor = new HashSet<Class<? extends OpenmrsObject>>();
	
	private final MetricsEventListener eventListener = new MetricsEventListener();
	
	private boolean hasStarted = false;
	
	public void start() {
		eventListener.setToken(token);
		
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
	
	public void setClassesToMonitor(Set<Class<? extends OpenmrsObject>> classesToMonitor) {
		if (hasStarted) {
			throw new IllegalStateException("cannot change classes to monitor while already running");
		}
		
		this.classesToMonitor = classesToMonitor;
	}
	
	//	public void setToken(DaemonToken token) {
	//		this.token = token;
	//	}
}
