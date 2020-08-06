package org.openmrs.module.metrics.api.utils;

import static org.openmrs.module.metrics.MetricsConstants.METRIC_ACTION_LIST;
import static org.openmrs.module.metrics.MetricsConstants.METRIC_CATEGORY_LIST;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.JvmAttributeGaugeSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.metrics.api.db.EventAction;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.model.MetricscConfigImpl;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.builder.JmxReportBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

	private JmxReportBuilderImpl jmxReportBuilder;

	@Autowired
	public void setJmxReportBuilder(JmxReportBuilderImpl jmxReportBuilder) {
		this.jmxReportBuilder = jmxReportBuilder;
	}

	public MetricRegistry buildMetricFlow(Date startRange, Date endRange) throws MetricsException {
		//fetch custom metrics
		long noOfNewPatients = getMetricService().getNewObjectsByGivenDateRangeAndCategory(startRange, endRange,
				METRIC_CATEGORY_LIST[0], EventAction.CREATED);
		Map<String, Integer> noOfEncounters = getMetricService().getEncounterObjectTypesCountByGivenDateRange(startRange,endRange);

		MetricRegistry metricRegistry;
		metricRegistry = jmxReportBuilder.initializeMetricRegistry();

		jmxReportBuilder.removeExisitingInstanceLevelMetrics(metricRegistry);
		metricRegistry = jmxReportBuilder.registerServerMetrics(metricRegistry);
		metricRegistry = jmxReportBuilder.registerNewMetric(metricRegistry,noOfEncounters, "newEncounters");
		metricRegistry = jmxReportBuilder.registerNewMetric(metricRegistry, noOfNewPatients, "newPatientsRegistered");

		return metricRegistry;
	}

	public MetricRegistry buildServerMetricFlow() throws MetricsException {
		MetricRegistry metricRegistry;
		metricRegistry = jmxReportBuilder.initializeMetricRegistry();

		jmxReportBuilder.removeExisitingInstanceLevelMetrics(metricRegistry);
		metricRegistry = jmxReportBuilder.registerServerMetrics(metricRegistry);

		return metricRegistry;
	}

	public MetricRegistry buildSystemMetricFlow(Class clazz, String dateRange, EventAction action) throws MetricsException {
		MetricRegistry metricRegistry;
		metricRegistry = jmxReportBuilder.initializeMetricRegistry();

		jmxReportBuilder.removeExisitingInstanceLevelMetrics(metricRegistry);

		Map<String, Integer> metricValueMap = getSingleMetricValue(clazz, dateRange, action);
		Map.Entry<String, Integer> firstEntry = metricValueMap.entrySet().iterator().next();
		metricRegistry = jmxReportBuilder.registerNewMetric(metricRegistry, firstEntry.getValue(), firstEntry.getKey());

		return metricRegistry;
	}

	public Map<String, Integer> getSingleMetricValue(Class clazz, String dateRange, EventAction action){
		Map<String, Integer> metricValueMap = new LinkedHashMap<>();
		switch (dateRange){
			case "today":
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "Today", action),
						getMetricService().fetchTotalOpenMRSObjectCountTodayByEventAction(clazz, action));
				break;
			case  "this_week":
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "ThisWeek", action),
						getMetricService().fetchTotalOpenMRSObjectCountThisWeekByEventAction(clazz, action));
				break;
			case  "last_week":
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "LastWeek", action),
						getMetricService().fetchTotalOpenMRSObjectCountLastWeekByEventAction(clazz, action));
				break;
			case  "this_month":
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "ThisMonth", action),
						getMetricService().fetchTotalOpenMRSObjectCountThisMonthByEventAction(clazz, action));
				break;
			case  "last_month":
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "LastMonth", action),
						getMetricService().fetchTotalOpenMRSObjectCountLastMonthByEventAction(clazz, action));
				break;
			case  "this_year":
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "ThisYear", action),
						getMetricService().fetchTotalOpenMRSObjectCountThisYearByEventAction(clazz, action));
				break;
			case  "last_year":
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "LastYear", action),
						getMetricService().fetchTotalOpenMRSObjectCountThisYearByEventAction(clazz, action));
				break;
			default:
				metricValueMap.put(getMetricNameBasedOnActionClassAndDateRange(clazz, "Today", action),
						getMetricService().fetchTotalOpenMRSObjectCountTodayByEventAction(clazz, action));
		}
		return metricValueMap;
	}

	public String getMetricNameBasedOnActionClassAndDateRange(Class clazz, String dateRange, EventAction action){
		StringBuilder metricName
				= new StringBuilder();
		switch (action.toString()){
			case "CREATED":
				metricName.append("new");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
				break;
			case  "UPDATED":
				metricName.append("updated");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
				break;
			case  "RETIRED":
				metricName.append("retired");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
				break;
			case  "UNRETIRED":
				metricName.append("unretired");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
				break;
			case  "VOIDED":
				metricName.append("voided");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
				break;
			case  "UNVOIDED":
				metricName.append("unvoided");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
				break;
			case  "DELETED":
				metricName.append("deleted");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
				break;
			default:
				metricName.append("new");
				metricName.append(clazz.toString());
				metricName.append(dateRange);
		}
		return metricName.toString();
	}

	private MetricService getMetricService() {
		return Context.getService(MetricService.class);
	}
}
