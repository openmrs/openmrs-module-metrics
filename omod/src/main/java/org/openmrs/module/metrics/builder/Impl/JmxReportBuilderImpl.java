package org.openmrs.module.metrics.builder.Impl;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jvm.JvmAttributeGaugeSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.openmrs.module.metrics.model.MetricscConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class JmxReportBuilderImpl implements JmxReportBuilder {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private MetricRegistry metricRegistry;
	
	@Override
	public MetricRegistry initializeMetricRegistry() {
		metricRegistry.register("jvm.attribute", new JvmAttributeGaugeSet());
		metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
		return metricRegistry;
	}
	
	@Override
	public JmxReporter start(MetricRegistry metricRegistry) {
		JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
		jmxReporter.start();
		return jmxReporter;
	}
	
	@Override
	public void stop(MetricRegistry metricRegistry) {
		JmxReporter.forRegistry(metricRegistry).build().stop();
	}
}
