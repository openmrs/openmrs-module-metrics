package org.openmrs.module.metrics.util;

import static org.openmrs.module.metrics.api.utils.EventsUtils.readResourceFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.jmx.JmxMeterRegistry;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.model.impl.GeneralEndPointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricHandler.class);

	ObjectMapper objMapper;

	Integer noOfNewPatients;

	Map<String, Integer> noOfEncounters;

	String currentKey;

	Supplier<Number> getEncounter = () -> {
		if (noOfEncounters != null) {
			return noOfEncounters.get(currentKey);
		} else {
			return 0.0;
		}
	};

	Supplier<Number> getNewPatients = () -> {
		return noOfNewPatients;
	};

	@Autowired
	private JmxMeterRegistry jmxMeterRegistry;

	@Autowired
	private MetricService metricService;

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

	public JmxMeterRegistry buildMetricFlow(LocalDateTime startRange, LocalDateTime endRange) throws MetricsException {

		//fetch custom metrics
		noOfNewPatients = metricService.getNewPatientsObjectsByGivenDateRange(startRange, endRange);
		noOfEncounters = metricService.getEncounterObjectTypesCountByGivenDateRange(startRange,
				endRange);

		//register encounter types in metric registry
		noOfEncounters.forEach((key, tab) -> {
			currentKey = key;
			Gauge.builder("statistic.enocuntersbytype", getEncounter)
					.tag("type", key)
					.description("The number of new encounters registered from encounter type" + key)
					.register(jmxMeterRegistry);
		});

		Gauge.builder("statistic.patientcount", getNewPatients)
				.tag("type", "Patient type")
				.description("Number of unserved orders")
				.register(jmxMeterRegistry);

		return jmxMeterRegistry;
	}

	public ObjectWriter getWriter(HttpServletRequest request) {
		return this.objMapper.writerWithDefaultPrettyPrinter();
	}
}
