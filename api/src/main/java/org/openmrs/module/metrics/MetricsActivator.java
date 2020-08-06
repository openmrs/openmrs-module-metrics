/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.metrics;

import static org.openmrs.module.metrics.MetricsConstants.EVENTS_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getOpenMrsClass;
import static org.openmrs.module.metrics.api.utils.EventsUtils.parseJsonFileToEventConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.metrics.api.event.MetricsEventListener;
import org.openmrs.module.metrics.api.model.EventConfiguration;
import org.openmrs.module.metrics.api.model.GeneralConfiguration;
import org.openmrs.module.metrics.api.service.EventConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MetricsActivator extends BaseModuleActivator implements DaemonTokenAware {
	
	private static final Logger log = LoggerFactory.getLogger(MetricsActivator.class);
	
	private static DaemonToken daemonToken = null;

	private Set<Class<? extends OpenmrsObject>> classesToMonitor = new HashSet<>();

	private boolean hasStarted = false;

	private HashMap<String, EventConfiguration> eventConfigurationByOpenMrsClass;

	private Set<Class<? extends OpenmrsObject>> openMrsClassesToMonitor;
	private EventConfigurationService eventConfigurationService;

	private final MetricsEventListener eventListener = new MetricsEventListener();
	
	/**
	 * @see #started()
	 */
	@Override
	public void started() {
		eventConfigurationService = Context.getService(EventConfigurationService.class);
		openMrsClassesToMonitor = eventConfigurationService.getClassesToMonitorFromConfiguration();
		eventListener.setToken(daemonToken);
		for (Class<? extends OpenmrsObject> classToMonitor : openMrsClassesToMonitor) {
			//all events
			Event.subscribe(classToMonitor, null, eventListener);
		}

		hasStarted = true;
		
		log.info("Started Metrics");
	}
	
	/**
	 * @see #stopped()
	 */
	@Override
	public void stopped() {
		for (Class<? extends OpenmrsObject> classToMonitor : openMrsClassesToMonitor) {
			Event.unsubscribe(classToMonitor,null, eventListener);
		}
		hasStarted = false;
		
		log.info("Shutdown Metrics");
	}
	
	@Override
	public void setDaemonToken(DaemonToken daemonToken) {
		MetricsActivator.daemonToken = daemonToken;
	}

	private void loadEventConfigurations(GeneralConfiguration generalConfiguration) {
		HashMap<String, EventConfiguration> byOpenMrsClass = new LinkedHashMap<>();
		Set<Class<? extends OpenmrsObject>> classesToMonitor =  new HashSet<>();

		for (EventConfiguration configuration : generalConfiguration.getEventConfigurations()) {
			byOpenMrsClass.put(configuration.getOpenMrsClass(), configuration);
			classesToMonitor.add(getOpenMrsClass(configuration.getOpenMrsClass()));
		}

		eventConfigurationByOpenMrsClass = byOpenMrsClass;
		openMrsClassesToMonitor = classesToMonitor;
	}
}
