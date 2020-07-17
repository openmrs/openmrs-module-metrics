package org.openmrs.module.metrics.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.OpenmrsObject;
import org.openmrs.event.Event;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.model.EventConfiguration;
import org.openmrs.module.metrics.api.model.GeneralConfiguration;
import org.openmrs.util.OpenmrsClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsUtils {
	
	private static Clock clock = Clock.systemUTC();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventsUtils.class);
	
	private static final String UUID_PATTERN = "{uuid}";
	
	public static boolean resourceFileExists(String path) {
		InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream(path);
		return in != null;
	}
	
	public static GeneralConfiguration parseJsonFileToEventConfiguration(String resourcePath) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			GeneralConfiguration eventConfiguration = mapper.readValue(readResourceFile(resourcePath),
			    GeneralConfiguration.class);
			return eventConfiguration;
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
	}
	
	public static String readResourceFile(String file) throws MetricsException {
		try (InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream(file)) {
			if (in == null) {
				throw new MetricsException("Resource '" + file + "' doesn't exist");
			}
			return IOUtils.toString(in, "UTF-8");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
	}
	
	public static Class<? extends OpenmrsObject> getOpenMrsClass(String openMrsClass) throws MetricsException {
		Class<? extends OpenmrsObject> classToMonitor;
		try {
			classToMonitor = (Class<? extends OpenmrsObject>) Class.forName(openMrsClass);
			return classToMonitor;
		}
		catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
	}
	
	public static MetricEvent buildEventObject(String simpleName, String uuid, Event.Action eventAction,
	        EventConfiguration eventConfiguration) {
		if (!eventConfiguration.isEnabled()) {
			LOGGER.debug("Skipped writing '{}' Event to the database because "
			        + "the synchronization for this object is disabled in the configuration", simpleName);
			return null;
		}
		
		final MetricEvent event = new MetricEvent(eventConfiguration.getTitle(), LocalDateTime.now(), null, getEventContent(
		    simpleName, uuid, eventConfiguration), eventConfiguration.getCategory(), eventAction.name(), LocalDateTime.now()//for now added current time stamp have to debug and see how the event object looks alike
		);
		
		return event;
	}
	
	private static String getEventContent(String simpleName, String uuid, EventConfiguration eventConfiguration) {

		String u_uid =  uuid;
		Map<String, String> urls = new HashMap<>();
		for (Map.Entry<String, String> entry : eventConfiguration.getLinkTemplates().entrySet()) {
			urls.put(entry.getKey(), entry.getValue().replace(UUID_PATTERN, u_uid));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(urls);
		} catch (IOException e) {
			throw new MetricsException("There is a problem with serialize resource links to Events content");
		}
	}
	
	public static Date now() {
		return Date.from(Instant.now(clock));
	}
}
