package org.openmrs.module.metrics.util;

import static com.codahale.metrics.MetricRegistry.name;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.JmxAttributeGauge;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.openmrs.module.metrics.model.MetricscConfigImpl;
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
	
	public MetricRegistry buildMetricFlow(LocalDateTime startRange, LocalDateTime endRange) throws MetricsException {
		
		//fetch custom metrics
		Integer noOfNewPatients = metricService.getNewPatientsObjectsByGivenDateRange(startRange, endRange);
		Map<String, Integer> noOfEncounters = metricService.getEncounterObjectTypesCountByGivenDateRange(startRange,
		    endRange);
		ObjectName objectName;
		
		try {
			objectName = new ObjectName("org.openmrs.module:metric=SystemMetrics");
		}
		catch (MalformedObjectNameException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
		
		//jmx report builder flow
		MetricRegistry metricRegistry = jmxReportBuilder.initializeMetricRegistry();
		metricRegistry.register(name(MetricscConfigImpl.class, "New patients registered"), new JmxAttributeGauge(objectName,
		        "newPatientsRegistered"));
		metricRegistry.register(name(MetricscConfigImpl.class, "New Encounter grouped by type"), new JmxAttributeGauge(
		        objectName, "newEncounters"));
		return metricRegistry;
	}
	
	public ObjectWriter getWriter(HttpServletRequest request) {
		return this.objMapper.writerWithDefaultPrettyPrinter();
	}
}
