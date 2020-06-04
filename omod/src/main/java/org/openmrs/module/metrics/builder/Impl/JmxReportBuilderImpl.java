package org.openmrs.module.metrics.builder.Impl;

import java.time.Duration;
import java.util.Collection;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.metrics.builder.JmxReportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JmxReportBuilderImpl implements JmxReportBuilder {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private CompositeMeterRegistry compositeMeterRegistry;
	
	public JmxReportBuilderImpl(CompositeMeterRegistry meterRegistry) {
		this.compositeMeterRegistry = meterRegistry;
	}
	
	@Override
	public CompositeMeterRegistry initializeMetricRegistry() {
		compositeMeterRegistry.add(jmxMeterRegistry());
		new JvmThreadMetrics().bindTo(compositeMeterRegistry);
		new JvmGcMetrics().bindTo(compositeMeterRegistry);
		new JvmMemoryMetrics().bindTo(compositeMeterRegistry);
		new ProcessorMetrics().bindTo(compositeMeterRegistry);
		new UptimeMetrics().bindTo(compositeMeterRegistry);
		return compositeMeterRegistry;
	}
	
	@Override
	public JmxMeterRegistry initializeJMXMetricRegistry() {
		JmxMeterRegistry jmxMeterRegistry = jmxMeterRegistry();
		new JvmThreadMetrics().bindTo(jmxMeterRegistry);
		new JvmGcMetrics().bindTo(jmxMeterRegistry);
		new JvmMemoryMetrics().bindTo(jmxMeterRegistry);
		new ProcessorMetrics().bindTo(jmxMeterRegistry);
		new UptimeMetrics().bindTo(jmxMeterRegistry);
		return jmxMeterRegistry;
	}
	
	@Bean
	public JmxMeterRegistry jmxMeterRegistry() {
		return new JmxMeterRegistry(new JmxConfig() {
			
			@Override
			public Duration step() {
				return Duration.ofSeconds(10);
			}
			
			@Override
			public String get(String k) {
				return null;
			}
		}, Clock.SYSTEM);
	}
	
	@Override
	public CompositeMeterRegistry addNewMeterrRegistry(MeterRegistry meterRegistry) {
		compositeMeterRegistry.add(meterRegistry);
		return compositeMeterRegistry;
	}
	
	@Override
	public CompositeMeterRegistry removeFromMeterregistry(MeterRegistry meterRegistry) {
		compositeMeterRegistry.remove(meterRegistry);
		return compositeMeterRegistry;
	}
}
