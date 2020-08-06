package org.openmrs.module.metrics.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.exceptions.MetricsException;
import org.openmrs.module.metrics.api.model.EventConfiguration;
import org.openmrs.module.metrics.api.model.GeneralConfiguration;
import org.openmrs.util.OpenmrsClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsUtils {

	private static Clock clock = Clock.systemUTC();

	private static final Logger LOGGER = LoggerFactory.getLogger(EventsUtils.class);

	private static final String UUID_PATTERN = "{uuid}";

	public static boolean resourceFileExists(String path) {
		InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream(path);
		return in != null;
	}

	public static GeneralConfiguration parseJsonFileToEventConfiguration(String resourcePath) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			GeneralConfiguration eventConfiguration = mapper.readValue(readResourceFile(resourcePath),
					GeneralConfiguration.class);
			return eventConfiguration;
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
	}

	public static String readResourceFile(String file) throws MetricsException {
		try (InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream(file)) {
			if (in == null) {
				throw new MetricsException("Resource '" + file + "' doesn't exist");
			}
			return IOUtils.toString(in, "UTF-8");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
	}

	public static Class<? extends OpenmrsObject> getOpenMrsClass(String openMrsClass) throws MetricsException {
		Class<? extends OpenmrsObject> classToMonitor;
		try {
			classToMonitor = (Class<? extends OpenmrsObject>) Class.forName(openMrsClass);
			return classToMonitor;
		}
		catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw new MetricsException(e);
		}
	}

	public static MetricEvent buildEventObject(String simpleName, String uuid, Event.Action eventAction,
			EventConfiguration eventConfiguration) {
		if (!eventConfiguration.isEnabled()) {
			LOGGER.debug("Skipped writing '{}' Event to the database because "
					+ "the synchronization for this object is disabled in the configuration", simpleName);
			return null;
		}

		StringBuilder tags = new StringBuilder(eventAction.name());

		final MetricEvent event = new MetricEvent(eventConfiguration.getTitle(), now(), uuid, getEventContent(simpleName,
				uuid, eventConfiguration), eventConfiguration.getCategory(), tags.toString()//for now added current time stamp have to debug and see how the event object looks alike
		);

		return event;
	}

	private static String getEventContent(String simpleName, String uuid, EventConfiguration eventConfiguration) {

		String u_uid =  uuid;
		Map<String, String> urls = new HashMap<>();
		for (Map.Entry<String, String> entry : eventConfiguration.getLinkTemplates().entrySet()) {
			urls.put(entry.getKey(), entry.getValue().replace(UUID_PATTERN, u_uid));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(urls);
		} catch (IOException e) {
			throw new MetricsException("There is a problem with serialize resource links to Events content");
		}
	}

	public static Date now() {
		return Date.from(Instant.now(clock));
	}

	public static Date getTodayDate() {
		Date date = Calendar.getInstance(Context.getLocale()).getTime();
		return date;
	}

	public static String getCategoryNameFromClass(@SuppressWarnings("rawtypes") Class clazz) {
		String cateogryName = "";

		if (clazz.equals(User.class)) {
			cateogryName = "user";
		} else if (clazz.equals(Location.class)) {
			cateogryName = "location";
		} else if (clazz.equals(Concept.class)) {
			cateogryName = "concept";
		} else if (clazz.equals(Form.class)) {
			cateogryName = "form";
		} else if (clazz.equals(Patient.class)) {
			cateogryName = "patient";
		} else if (clazz.equals(Order.class)) {
			cateogryName = "orders";
		} else if (clazz.equals(Visit.class)) {
			cateogryName = "visit";
		} else if (clazz.equals(Obs.class)) {
			cateogryName = "obs";
		} else if (clazz.equals(Encounter.class)) {
			cateogryName = "encounter";
		}
		return cateogryName;
	}

	public static LocalDate getToday() {
		LocalDate today = LocalDate.now();
		return today;
	}

	public static Date getThisWeekStartDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofWeeks(1));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getThisWeekEndDate() {
		LocalDate today = getToday();
		return Date.from(today.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getLastWeekEndDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofWeeks(1));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getLastWeekStartDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofWeeks(2));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getLastMonthEndDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofMonths(1));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getLastMonthStartDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofMonths(2));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getThisMonthStartDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofMonths(1));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getThisMonthEndDate() {
		LocalDate today = getToday();
		return Date.from(today.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getThisYearStartDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofYears(1));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getThisYearEndDate() {
		LocalDate today = getToday();
		return Date.from(today.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getLastYearStartDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofYears(2));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getLastYearEndDate() {
		LocalDate today = getToday();
		LocalDate lastWeek = today.minus(Period.ofYears(1));

		return Date.from(lastWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
}
