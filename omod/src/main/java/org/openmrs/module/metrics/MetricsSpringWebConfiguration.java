package org.openmrs.module.metrics;

import java.time.Duration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsSpringWebConfiguration {
	
	@Bean
	public JmxMeterRegistry jmxMeterRegistry() {
		JmxMeterRegistry jmxMeterRegistry = new JmxMeterRegistry(new JmxConfig() {
			
			@Override
			public Duration step() {
				return Duration.ofSeconds(10);
			}
			
			@Override
			public String get(String k) {
				return null;
			}
		}, Clock.SYSTEM);
		
		new JvmThreadMetrics().bindTo(jmxMeterRegistry);
		new JvmGcMetrics().bindTo(jmxMeterRegistry);
		new JvmMemoryMetrics().bindTo(jmxMeterRegistry);
		new ProcessorMetrics().bindTo(jmxMeterRegistry);
		new UptimeMetrics().bindTo(jmxMeterRegistry);
		
		return jmxMeterRegistry;
	}
}
