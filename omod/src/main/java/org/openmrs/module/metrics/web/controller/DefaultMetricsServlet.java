package org.openmrs.module.metrics.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.SortedMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.util.MetricHandler;

public class DefaultMetricsServlet extends HttpServlet {

	private transient MetricRegistry metricRegistry;

	private transient ObjectMapper objMapper;

	private LocalDateTime startDatetime;

	private LocalDateTime endDatetime;

	private static final String CONTENT_TYPE = "application/json";

	public DefaultMetricsServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		if (req.getParameter("startDateTime") != null && req.getParameter("endDateTime") != null) {
			this.startDatetime = LocalDateTime.parse(req.getParameter("startDateTime"));
			this.endDatetime = LocalDateTime.parse(req.getParameter("endDatetime"));
			this.metricRegistry = MetricHandler.buildMetricFlow(startDatetime, endDatetime);
		}
		resp.setContentType(CONTENT_TYPE);
		resp.setStatus(HttpServletResponse.SC_OK);
		final OutputStream output = resp.getOutputStream();

		try {
			Object outputValue = filter(this.metricRegistry, req.getParameter("type"));
			MetricHandler.getWriter(req, this.objMapper).writeValue(output, outputValue);
		}
		catch (IOException e) {
			throw new MetricsException(e);
		}
		finally {
			output.close();
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
