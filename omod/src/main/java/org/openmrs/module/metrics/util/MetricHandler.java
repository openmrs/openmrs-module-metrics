package org.openmrs.module.metrics.util;

import static com.codahale.metrics.MetricRegistry.name;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.Map;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jvm.JmxAttributeGauge;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.api.utils.EventsUtils;
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.openmrs.module.metrics.model.MetricscConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MetricHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MetricHandler.class);
	
	@Autowired
	private JmxReportBuilder jmxReportBuilder;
	
	@Autowired
	private MetricService metricService;
	
	public void buildMetricFlow(LocalDateTime startRange, LocalDateTime endRange) throws MetricsException {
		
		//fetch custom metrics
		Integer noOfNewPatients = metricService.getNewPatientsObjectsByGivenDateRange(startRange, endRange);
		Map<String, Integer> noOfEncounters = metricService.getEncounterObjectTypesCountByGivenDateRange(startRange,
		    endRange);
		
		ObjectName objectName;
		
		//register custom metrics with jmx
		MetricscConfigImpl metricConfigMBean = new MetricscConfigImpl(noOfNewPatients, noOfEncounters);
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			objectName = new ObjectName("com.sysdig.app:name=SystemStatusExample");
			//			mbs.registerMBean(metricConfigMBean, objectName);
		}
		catch (MalformedObjectNameException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
		//		catch (InstanceAlreadyExistsException e) {
		//			LOGGER.error(e.getMessage());
		//			throw new MetricsException(e);
		//		}
		//		catch (NotCompliantMBeanException e) {
		//			LOGGER.error(e.getMessage());
		//			throw new MetricsException(e);
		//		}
		//		catch (MBeanRegistrationException e) {
		//			LOGGER.error(e.getMessage());
		//			throw new MetricsException(e);
		//		}
		
		//jmx report builder flow
		MetricRegistry metricRegistry = jmxReportBuilder.initializeMetricRegistry();
		metricRegistry.register(name(MetricscConfigImpl.class, "New patients registered"), new JmxAttributeGauge(objectName,
		        "nePatientsRegistered"));
		metricRegistry.register(name(MetricscConfigImpl.class, "New Encounterby grouped by type"), new JmxAttributeGauge(
		        objectName, "newEncounters"));
		JmxReporter jmxReport = jmxReportBuilder.start(metricRegistry);
	}
}
