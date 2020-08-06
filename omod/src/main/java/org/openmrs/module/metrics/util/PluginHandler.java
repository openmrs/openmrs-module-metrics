package org.openmrs.module.metrics.util;

import static org.openmrs.module.metrics.api.utils.EventsUtils.readResourceFile;

import java.io.IOException;

import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.model.impl.GeneralEndPointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class PluginHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginHandler.class);
	
	public static GeneralEndPointConfig parseJsonFileToEndPointConfiguration(String resourcePath) {
		org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
		try {
			GeneralEndPointConfig endPointConfig = mapper.readValue(readResourceFile(resourcePath),
			    GeneralEndPointConfig.class);
			return endPointConfig;
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
	}
}
