//package org.openmrs.module.metrics.web;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.Date;
//import java.util.Locale;
//import java.util.concurrent.TimeUnit;
//
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.codahale.metrics.Counter;
//import com.codahale.metrics.Histogram;
//import com.codahale.metrics.MetricFilter;
//import com.codahale.metrics.MetricRegistry;
//import com.codahale.metrics.json.MetricsModule;
//import com.codahale.metrics.servlets.MetricsServlet;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.util.JSONPObject;
//import org.openmrs.module.metrics.util.MetricHandler;
//
//public class DefaultMetricsServletcccccc extends HttpServlet {
//
//	//	MetricHandler metricHandler;
//
//	private static MetricRegistry METRIC_REGISTRY = new MetricRegistry();
//
//	public static final String RATE_UNIT = MetricsServlet.class.getCanonicalName() + ".rateUnit";
//
//	public static final String DURATION_UNIT = MetricsServlet.class.getCanonicalName() + ".durationUnit";
//
//	public static final String SHOW_SAMPLES = MetricsServlet.class.getCanonicalName() + ".showSamples";
//
//	public static final String METRICS_REGISTRY = MetricsServlet.class.getCanonicalName() + ".registry";
//
//	public static final String ALLOWED_ORIGIN = MetricsServlet.class.getCanonicalName() + ".allowedOrigin";
//
//	public static final String METRIC_FILTER = MetricsServlet.class.getCanonicalName() + ".metricFilter";
//
//	public static final String CALLBACK_PARAM = MetricsServlet.class.getCanonicalName() + ".jsonpCallback";
//
//	private static final long serialVersionUID = 1049773947734939602L;
//
//	private static final String CONTENT_TYPE = "application/json";
//
//	protected String allowedOrigin;
//
//	protected String jsonpParamName;
//
//	protected transient MetricRegistry registry;
//
//	protected transient ObjectMapper mapper;
//
//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		super.init(config);
//		final ServletContext context = config.getServletContext();
//		context.setAttribute(METRICS_REGISTRY, getMetricRegistry());
//		context.setAttribute(METRIC_FILTER, getMetricFilter());
//		if (null == registry) {
//			final Object registryAttr = context.getAttribute(METRICS_REGISTRY);
//			if (registryAttr instanceof MetricRegistry) {
//				this.registry = (MetricRegistry) registryAttr;
//			} else {
//				throw new ServletException("Couldn't find a MetricRegistry instance.");
//			}
//		}
//		this.allowedOrigin = context.getInitParameter(ALLOWED_ORIGIN);
//		this.jsonpParamName = context.getInitParameter(CALLBACK_PARAM);
//
//		//		setupMetricsModule(context);
//	}
//
//	protected void setupMetricsModule(ServletContext context) {
//		final TimeUnit rateUnit = parseTimeUnit(context.getInitParameter(RATE_UNIT), TimeUnit.SECONDS);
//		final TimeUnit durationUnit = parseTimeUnit(context.getInitParameter(DURATION_UNIT), TimeUnit.SECONDS);
//		final boolean showSamples = Boolean.parseBoolean(context.getInitParameter(SHOW_SAMPLES));
//		MetricFilter filter = (MetricFilter) context.getAttribute(METRIC_FILTER);
//		if (filter == null) {
//			filter = MetricFilter.ALL;
//		}
//
//		this.mapper = new ObjectMapper().registerModule(new MetricsModule(rateUnit, durationUnit, showSamples, filter));
//	}
//
//	@Override
//	protected void doGet(HttpServletRequest req,
//			HttpServletResponse resp) throws ServletException, IOException {
//		resp.setContentType(CONTENT_TYPE);
//		if (allowedOrigin != null) {
//			resp.setHeader("Access-Control-Allow-Origin", allowedOrigin);
//		}
//		resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
//		resp.setStatus(HttpServletResponse.SC_OK);
//
//		try (OutputStream output = resp.getOutputStream()) {
//			if (jsonpParamName != null && req.getParameter(jsonpParamName) != null) {
//				getWriter(req).writeValue(output, new JSONPObject(req.getParameter(jsonpParamName), registry));
//			} else {
//				getWriter(req).writeValue(output, registry);
//			}
//		}
//	}
//
//	protected ObjectWriter getWriter(HttpServletRequest request) {
//		final boolean prettyPrint = Boolean.parseBoolean(request.getParameter("pretty"));
//		if (prettyPrint) {
//			return mapper.writerWithDefaultPrettyPrinter();
//		}
//		return mapper.writer();
//	}
//
//	protected TimeUnit parseTimeUnit(String value, TimeUnit defaultValue) {
//		try {
//			return TimeUnit.valueOf(String.valueOf(value).toUpperCase(Locale.US));
//		}
//		catch (IllegalArgumentException e) {
//			return defaultValue;
//		}
//	}
//
//	protected MetricRegistry getMetricRegistry() {
//		Counter counter = METRIC_REGISTRY.counter("m01-counter");
//		counter.inc();
//
//		Histogram histogram = METRIC_REGISTRY.histogram("m02-histogram");
//		histogram.update(5);
//		histogram.update(20);
//		histogram.update(100);
//		return METRIC_REGISTRY;
//	}
//
//	protected MetricFilter getMetricFilter() {
//		// use the default
//		return MetricFilter.ALL;
//	}
//
//	public void setMetricHandler(MetricHandler metricHandler) {
//
//	}
//}
