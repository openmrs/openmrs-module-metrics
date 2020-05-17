package org.openmrs.module.metrics.util;

import java.time.LocalDateTime;
import java.util.Map;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.openmrs.module.metrics.model.MetricscConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class MetricHandler {
	
	@Autowired
	private JmxReportBuilder jmxReportBuilder;
	
	@Autowired
	private MetricService metricService;
	
	public void buildMetricFlow(LocalDateTime startRange, LocalDateTime endRange) {
		
		//fetch custom metrics
		Integer noOfNewPatients = metricService.getNewPatientsObjectsByGivenDateRange(startRange, endRange);
		Map<String, Integer> noOfEncounters = metricService.getEncounterObjectTypesCountByGivenDateRange(startRange,
		    endRange);
		
		//register custom metrics with jmx
		MetricscConfigImpl metricConfigMBean = new MetricscConfigImpl(noOfNewPatients, noOfEncounters);
		MetricRegistry metricRegistry = jmxReportBuilder.initializeMetricRegistry();
		JmxReporter jmxReport = jmxReportBuilder.start(metricRegistry);
	}
}
