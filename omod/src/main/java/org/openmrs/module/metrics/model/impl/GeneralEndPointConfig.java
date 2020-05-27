package org.openmrs.module.metrics.model.impl;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class GeneralEndPointConfig {
	
	private List<GraphiteConfiguration> graphiteConfigurations;
	
	public GeneralEndPointConfig() {
			graphiteConfigurations = new ArrayList<>();
		}
	
	public GeneralEndPointConfig(List<GraphiteConfiguration> graphiteConfigurations) {
		this.graphiteConfigurations = graphiteConfigurations;
	}
	
	public List<GraphiteConfiguration> getGraphiteConfigurations() {
		return graphiteConfigurations;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		org.openmrs.module.metrics.model.impl.GeneralEndPointConfig that = (org.openmrs.module.metrics.model.impl.GeneralEndPointConfig) o;
		return Objects.equals(graphiteConfigurations, that.graphiteConfigurations);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(graphiteConfigurations);
	}
	
}
