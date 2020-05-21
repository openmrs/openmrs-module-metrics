package org.openmrs.module.metrics.builder;

import java.time.LocalDateTime;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import org.openmrs.module.metrics.web.filter.RedirectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public interface JmxReportBuilder {
	
	MetricRegistry initializeMetricRegistry();
	
	JmxReporter start(MetricRegistry metricRegistry);
	
	void stop(MetricRegistry metricRegistry);
}
