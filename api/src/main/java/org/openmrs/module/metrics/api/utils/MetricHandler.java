package org.openmrs.module.metrics.api.utils;

import static org.openmrs.module.metrics.MetricsConstants.METRIC_LIST;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.model.MetricscConfigImpl;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.builder.JmxReportBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricHandler.class);

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

	private JmxReportBuilderImpl jmxReportBuilder = new JmxReportBuilderImpl();

//	public void setJmxReportBuilder(JmxReportBuilderImpl jmxReportBuilder) {
//		this.jmxReportBuilder = jmxReportBuilder;
//	}
//	public static GeneralEndPointConfig parseJsonFileToEndPointConfiguration(String resourcePath) {
//		org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
//		try {
//			GeneralEndPointConfig endPointConfig = mapper.readValue(readResourceFile(resourcePath),
//					GeneralEndPointConfig.class);
//			return endPointConfig;
//		}
//		catch (IOException e) {
//			LOGGER.error(e.getMessage());
//			throw new MetricsException(e);
//		}
//	}

	public MetricRegistry buildMetricFlow(Date startRange, Date endRange) throws MetricsException {
		//fetch custom metrics
		Integer noOfNewPatients = getMetricService().getNewPatientsObjectsByGivenDateRange(startRange, endRange);
		Map<String, Integer> noOfEncounters = getMetricService().getEncounterObjectTypesCountByGivenDateRange(startRange,endRange);
		ObjectName objectName;

		try {
			objectName = new ObjectName("org.openmrs.module:metric=SystemMetrics");
		}
		catch (MalformedObjectNameException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}

		MetricRegistry metricRegistry;
		//jmx report builder flow
		if(jmxReportBuilder.getMetricRegistry() == null){
			metricRegistry = jmxReportBuilder.initializeMetricRegistry();
		}else {
			metricRegistry = jmxReportBuilder.getMetricRegistry();
		}

		if(metricRegistry.getNames().contains(METRIC_LIST[0])){
			metricRegistry.remove(METRIC_LIST[0]);
		}
		metricRegistry = jmxReportBuilder.registerNewMetric(metricRegistry,noOfEncounters, "newEncounters");

		if(metricRegistry.getNames().contains(METRIC_LIST[1])){
			metricRegistry.remove(METRIC_LIST[1]);
		}
		metricRegistry = jmxReportBuilder.registerNewMetric(metricRegistry, noOfNewPatients, "newPatientsRegistered");

		return metricRegistry;
	}

	private MetricService getMetricService() {
		return Context.getService(MetricService.class);
	}

}
