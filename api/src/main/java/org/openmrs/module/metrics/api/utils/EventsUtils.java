package org.openmrs.module.metrics.api.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.model.GeneralConfiguration;
import org.openmrs.util.OpenmrsClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventsUtils.class);
	
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
}
