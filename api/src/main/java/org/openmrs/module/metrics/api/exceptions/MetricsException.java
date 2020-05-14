package org.openmrs.module.metrics.api.exceptions;

public class MetricsException extends RuntimeException {
	
	public MetricsException(String message) {
		super(message);
	}
	
	public MetricsException(Throwable cause) {
		super(cause);
	}
	
	public MetricsException(String message, Throwable cause) {
		super(message, cause);
	}
}
