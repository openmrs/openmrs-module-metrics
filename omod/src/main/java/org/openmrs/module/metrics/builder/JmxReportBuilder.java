package org.openmrs.module.metrics.builder;

import java.time.LocalDateTime;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.jmx.JmxMeterRegistry;
import org.openmrs.module.metrics.web.filter.RedirectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public interface JmxReportBuilder {
	
	CompositeMeterRegistry initializeMetricRegistry();
	
	CompositeMeterRegistry removeFromMeterregistry(MeterRegistry meterRegistry);
	
	CompositeMeterRegistry addNewMeterrRegistry(MeterRegistry meterRegistry);
	
	JmxMeterRegistry initializeJMXMetricRegistry();
	
}
