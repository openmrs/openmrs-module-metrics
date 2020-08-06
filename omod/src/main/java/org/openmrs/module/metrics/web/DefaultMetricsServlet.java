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

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.utils.MetricHandler;
import org.openmrs.module.metrics.builder.JmxReportBuilderImpl;

public class DefaultMetricsServlet extends MetricsServlet {
	
	public static final String METRICS_REGISTRY = MetricsServlet.class.getCanonicalName() + ".registry";
	
	public static final String METRIC_FILTER = MetricsServlet.class.getCanonicalName() + ".metricFilter";
	
	private static final long serialVersionUID = 1049773947734939602L;
	
	private static final String CONTENT_TYPE = "application/json";
	
	protected String allowedOrigin;
	
	protected String jsonpParamName;
	
	protected transient ObjectMapper mapper = new ObjectMapper();
	
	private static MetricRegistry METRIC_REGISTRY = new MetricRegistry();
	
	private MetricHandler metricHandler = new MetricHandler();
	
	JmxReportBuilderImpl jmxReportBuilder = new JmxReportBuilderImpl();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		final ServletContext context = config.getServletContext();
		context.setAttribute(METRICS_REGISTRY, jmxReportBuilder.initializeMetricRegistry());
		context.setAttribute(METRIC_FILTER, getMetricFilter());
	}
	
	protected MetricRegistry getMetricRegistry() {
		Counter counter = METRIC_REGISTRY.counter("m01-counter");
		counter.inc();
		
		Histogram histogram = METRIC_REGISTRY.histogram("m02-histogram");
		histogram.update(5);
		histogram.update(20);
		histogram.update(100);
		return METRIC_REGISTRY;
	}
	
	//	public void setMetricHandler(MetricHandler metricHandler) {
	//		this.metricHandler = metricHandler;
	//	}
	
	protected MetricFilter getMetricFilter() {
		// use the default
		return MetricFilter.ALL;
	}
	
	@Override
	protected void doGet(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(CONTENT_TYPE);
		if (allowedOrigin != null) {
			resp.setHeader("Access-Control-Allow-Origin", allowedOrigin);
		}

		Date startDatetime = new Date();
		Date endDatetime = new Date();
		MetricRegistry meterRegistry;
		final String CONTENT_TYPE = "application/json";

		if (req.getParameter("startDateTime") != null && req.getParameter("endDateTime") != null
				&& req.getParameter("type") != null) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				startDatetime = simpleDateFormat.parse(req.getParameter("startDateTime"));
				endDatetime =  simpleDateFormat.parse(req.getParameter("endDateTime"));
				String abn = req.getParameter("endDateTime");
			}
			catch (ParseException e) {
				throw new MetricsException(e);
			}
		}

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
