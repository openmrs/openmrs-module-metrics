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
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.openmrs.module.metrics.model.impl.GeneralEndPointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MetricHandler.class);
	
	@Autowired
	private JmxReportBuilder jmxReportBuilder;
	
	@Autowired
	private MetricService metricService;
	
	ObjectMapper objMapper;
	Integer noOfNewPatients;
	Map<String, Integer> noOfEncounters;
	String currentKey;

	public JmxMeterRegistry buildMetricFlow(LocalDateTime startRange, LocalDateTime endRange) throws MetricsException {
		
		//fetch custom metrics
		noOfNewPatients = metricService.getNewPatientsObjectsByGivenDateRange(startRange, endRange);
		noOfEncounters = metricService.getEncounterObjectTypesCountByGivenDateRange(startRange,
		    endRange);

		//jmx report builder flow
		JmxMeterRegistry meterRegistry = jmxReportBuilder.initializeJMXMetricRegistry();

		//register encounter types in metric registry
		noOfEncounters.forEach((key, tab) -> {
			currentKey = key;
			Gauge.builder("statistic.enocuntersbytype" ,getEncounnter::get)
					.tag("type", key)
					.description("The number of new encounters registerd from encounter type" + key)
					.register(meterRegistry);
		});

		Gauge.builder("statistic.patientcount", getNewPatients::get)
				.tag("type", "Patient type")
				.description("Number of unserved orders")
				.register(meterRegistry);

		return meterRegistry;
	}

	public ObjectWriter getWriter(HttpServletRequest request) {
		return this.objMapper.writerWithDefaultPrettyPrinter();
	}
	
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

	Supplier<Number> getEncounnter=()->{
		if(noOfEncounters != null) {
			return noOfEncounters.get(currentKey);
		}else
		{
			return 0.0;
		}
	};

	Supplier<Number> getNewPatients=()->{
		return noOfNewPatients;
	};
}
