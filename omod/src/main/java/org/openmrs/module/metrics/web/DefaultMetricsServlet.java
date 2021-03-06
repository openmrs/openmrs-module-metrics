package org.openmrs.module.metrics.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.utils.MetricHandler;
import org.openmrs.module.metrics.builder.JmxReportBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class DefaultMetricsServlet extends MetricsServlet {
	
	public static final String METRICS_REGISTRY = MetricsServlet.class.getCanonicalName() + ".registry";
	
	public static final String METRIC_FILTER = MetricsServlet.class.getCanonicalName() + ".metricFilter";
	
	private static final String CONTENT_TYPE = "application/json";
	
	protected String allowedOrigin;
	
	protected String jsonpParamName;
	
	protected transient ObjectMapper mapper = new ObjectMapper();
	
	MetricHandler metricHandler;
	
	JmxReportBuilderImpl jmxReportBuilder;
	
	@Autowired
	public void setJmxReportBuilder(JmxReportBuilderImpl jmxReportBuilder) {
		this.jmxReportBuilder = jmxReportBuilder;
	}
	
	@Autowired
	public void setMetricHandler(MetricHandler metricHandler) {
		this.metricHandler = metricHandler;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		final ServletContext context = config.getServletContext();
		context.setAttribute(METRICS_REGISTRY, this.jmxReportBuilder.initializeMetricRegistry());
		context.setAttribute(METRIC_FILTER, getMetricFilter());
		super.init(config);
		
	}
	
	protected MetricFilter getMetricFilter() {
		// use the default
		return MetricFilter.ALL;
	}
	
	@Override
	protected void doGet(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		if (allowedOrigin != null) {
			resp.setHeader("Access-Control-Allow-Origin", allowedOrigin);
		}

		Date startDatetime = new Date();
		Date endDatetime = new Date();
		MetricRegistry meterRegistry;

		if (req.getParameter("startDateTime") != null && req.getParameter("endDateTime") != null
				&& req.getParameter("type") != null) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				startDatetime = simpleDateFormat.parse(req.getParameter("startDateTime"));
				endDatetime =  simpleDateFormat.parse(req.getParameter("endDateTime"));
			}
			catch (ParseException e) {
				throw new MetricsException(e);
			}
		}

		resp.setContentType(CONTENT_TYPE);
		resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
		resp.setStatus(HttpServletResponse.SC_OK);

		try (OutputStream output = resp.getOutputStream()) {
			if (jsonpParamName != null && req.getParameter(jsonpParamName) != null) {
				getWriter(req).writeValue(output, new JSONPObject(req.getParameter(jsonpParamName), metricHandler.buildMetricFlow(startDatetime,endDatetime)));
			} else {
				meterRegistry = metricHandler.buildMetricFlow(startDatetime,endDatetime);

				getWriter(req).writeValue(output, meterRegistry);
			}
		}
	}
	
	protected ObjectWriter getWriter(HttpServletRequest request) {
		final boolean prettyPrint = Boolean.parseBoolean(request.getParameter("pretty"));
		if (prettyPrint) {
			return mapper.writerWithDefaultPrettyPrinter();
		}
		return mapper.writer();
	}
	
	protected TimeUnit parseTimeUnit(String value, TimeUnit defaultValue) {
		try {
			return TimeUnit.valueOf(String.valueOf(value).toUpperCase(Locale.US));
		}
		catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}
}
