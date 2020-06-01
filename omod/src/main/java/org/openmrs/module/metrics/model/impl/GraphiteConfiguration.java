package org.openmrs.module.metrics.model.impl;

public class GraphiteConfiguration {
	
	private String grpahiteHost;
	
	private int graphitePort;
	
	private boolean enabled;
	
	private int timeForPoll;
	
	private String graphitePrefix;
	
	public GraphiteConfiguration() {
	}
	
	public GraphiteConfiguration(String grpahiteHost, int graphitePort, int timeForPoll, String graphitePrefix) {
		this.graphitePort = graphitePort;
		this.enabled = false;
		this.grpahiteHost = grpahiteHost;
		this.timeForPoll = timeForPoll;
		this.graphitePrefix = graphitePrefix;
	}
	
	public String getGrpahiteHost() {
		return grpahiteHost;
	}
	
	public int getGraphitePort() {
		return graphitePort;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public int getTimeForPoll() {
		return timeForPoll;
	}
	
	public String getGraphitePrefix() {
		return graphitePrefix;
	}
}
