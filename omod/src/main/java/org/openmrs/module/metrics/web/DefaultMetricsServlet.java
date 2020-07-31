package org.openmrs.module.metrics.web;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import io.micrometer.jmx.JmxMeterRegistry;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.util.MetricHandler;
import org.openmrs.module.metrics.web.filter.RedirectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@Component
public class DefaultMetricsServlet extends MetricsServlet {
	
	@Autowired
	MetricHandler metricHandler;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		
		super.init(config);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Date startDatetime = new Date();
		Date endDatetime = new Date();
		JmxMeterRegistry meterRegistry;
		final String CONTENT_TYPE = "application/json";

		if (req.getParameter("startDateTime") != null && req.getParameter("endDateTime") != null) {

			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				startDatetime = simpleDateFormat.parse(req.getParameter("startDateTime"));
				endDatetime =  simpleDateFormat.parse(req.getParameter("endDateTime"));
			}
			catch (ParseException e) {
				throw new MetricsException(e);
			}

			meterRegistry = this.metricHandler.buildMetricFlow(startDatetime, endDatetime);
			resp.setContentType(CONTENT_TYPE);
			resp.setStatus(HttpServletResponse.SC_OK);

			try (OutputStream output = resp.getOutputStream()) {
				Object outputValue = filter(meterRegistry, req.getParameter("type"));
				this.metricHandler.getWriter(req).writeValue(output, outputValue);
			}
			catch (IOException e) {
				throw new MetricsException(e);
			}
		}
	}
	
	private Object filter(JmxMeterRegistry meterRegistry, String type) throws MetricsException {
		boolean filterByType = type != null && !type.isEmpty();
		
		if (filterByType) {
			SortedMap<String, ? extends Metric> metrics;
			
			switch (type) {
				case "gauges":
					metrics = meterRegistry.getDropwizardRegistry().getGauges();
					break;
				case "histograms":
					metrics = meterRegistry.getDropwizardRegistry().getHistograms();
					break;
				default:
					throw new MetricsException("Invalid metric type: " + type);
			}
			
			return metrics;
		}
		
		return meterRegistry;
	}
}
