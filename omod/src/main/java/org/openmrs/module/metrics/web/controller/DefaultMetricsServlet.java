package org.openmrs.module.metrics.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.util.MetricHandler;
import org.openmrs.module.metrics.web.filter.RedirectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultMetricsServlet extends MetricsServlet {
	
	private static final Logger log = LoggerFactory.getLogger(RedirectFilter.class);
	
	@Autowired
	MetricHandler metricHandler;
	
	public DefaultMetricsServlet() {
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		LocalDateTime startDatetime;
		LocalDateTime endDatetime;
		MetricRegistry metricRegistry;
		final String CONTENT_TYPE = "application/json";
		
		if (req.getParameter("startDateTime") != null && req.getParameter("endDateTime") != null) {
			startDatetime = LocalDateTime.parse(req.getParameter("startDateTime"));
			endDatetime = LocalDateTime.parse(req.getParameter("endDatetime"));
			metricRegistry = this.metricHandler.buildMetricFlow(startDatetime, endDatetime);
			
			resp.setContentType(CONTENT_TYPE);
			resp.setStatus(HttpServletResponse.SC_OK);
			final OutputStream output = resp.getOutputStream();
			
			try {
				Object outputValue = filter(metricRegistry, req.getParameter("type"));
				this.metricHandler.getWriter(req).writeValue(output, outputValue);
			}
			catch (IOException e) {
				throw new MetricsException(e);
			}
			finally {
				output.close();
			}
		}
	}
	
	private Object filter(MetricRegistry metricRegistry, String type) throws MetricsException {
		boolean filterByType = type != null && !type.isEmpty();
		
		if (filterByType) {
			SortedMap<String, ? extends Metric> metrics;
			if ("gauges".equals(type)) {
				metrics = metricRegistry.getGauges();
			} else if ("histograms".equals(type)) {
				metrics = metricRegistry.getHistograms();
			} else {
				throw new MetricsException("Invalid metric type");
			}
			return metrics;
		}
		return metricRegistry;
	}
}
