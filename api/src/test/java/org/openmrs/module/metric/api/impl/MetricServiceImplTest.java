package org.openmrs.module.metric.api.impl;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.equalTo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.EventAction;
import org.openmrs.module.metrics.api.db.MetricsDAO;
import org.openmrs.module.metrics.api.impl.MetricsServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class MetricServiceImplTest {

	private static final String METRIC_UUID = "5d6da0f0-44f3-44df-a0bd-b7f36ab4ab65";

	private static final String UNKNOWN_METRIC_UUID = "cvdasdsa-3608-496d-9025-asafasfas";

	private static final Integer METRIC_ID = 5;

	private static final String METRIC_UNKNOW_START_DATE = "1999-04-04";

	private static final String METRIC_KNOWN_START_DATE = "2020-07-08";

	private static final String METRIC_UNKNOW_END_DATE = "1999-04-10";

	private static final String METRIC_KNOWN_END_DATE = "2020-07-10";

	private static final String METRIC_KNOWN_START_DATETIME = "2020-07-08 16:23:45.0";

	private static final String METRIC_KNOWN_END_DATETIME = "2020-07-09 18:23:45.0";

	private static final String METRIC_UNKNOW_START_DATETIME = "1993-07-08 16:23:45.0";

	private static final String METRIC_UNKNOW_END_DATETIME = "1999-07-09 18:23:45.0";

	SimpleDateFormat simpleDateFormatWithoutTime = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat simpleDateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	String sample_category = "patient";

	String action = "CREATED";

	@Mock
	private MetricsDAO metricsDAO;

	private MetricsServiceImpl metricsService;

	private MetricEvent metricEvent;

	private Map<String, Integer> encounterMap;

	@Before
	public void setup() {
		metricsService = new MetricsServiceImpl();
		metricsService.setMetricsDAO(metricsDAO);

		metricEvent = new MetricEvent();
		metricEvent.setId(5);
		metricEvent.setUuid(METRIC_UUID);
		encounterMap = new HashMap<>();
	}

	@Test
	public void getMetricEventByUuid_shouldGetMetricEventByUuid() {
		when(metricsDAO.getMetricEventByUuid(METRIC_UUID)).thenReturn(metricEvent);

		MetricEvent result = metricsService.getMetricEventByUuid(METRIC_UUID);
		assertThat(result, notNullValue());
		assertThat(result.getId(), notNullValue());
		assertThat(result.getId(), equalTo(METRIC_ID));
	}

	@Test
	public void getMetricEventByUuid_shouldReturnNullWhenCalledWithWrongUuid() {
		MetricEvent result = metricsService.getMetricEventByUuid(UNKNOWN_METRIC_UUID);
		assertThat(result, nullValue());
	}

	@Test
	public void saveMetricEvent_shouldSaveNewMetricEvent() {
		when(metricsDAO.saveMetricEvent(metricEvent)).thenReturn(metricEvent);

		MetricEvent result = metricsService.saveMetricEvent(metricEvent);
		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(METRIC_ID));
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldReturnWhenCorrectDateRangeParsed() throws ParseException {
		Date startDate = parseDateRanges(METRIC_KNOWN_START_DATE);
		Date endDate = parseDateRanges(METRIC_KNOWN_END_DATE);
		when(metricsDAO.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category, EventAction.CREATED))
				.thenReturn(1);

		int result = metricsService.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category,
				EventAction.CREATED);
		assertThat(result, notNullValue());
		assertThat(result, Matchers.equalTo(1));
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldNotReturnWhenCorrectDateRangeParsed() throws ParseException {
		Date startDate = parseDateRanges(METRIC_UNKNOW_START_DATE);
		Date endDate = parseDateRanges(METRIC_UNKNOW_END_DATE);
		when(metricsDAO.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category, EventAction.CREATED))
				.thenReturn(0);

		int result = metricsService.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category,
				EventAction.CREATED);
		assertThat(result, Matchers.equalTo(0));
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldReturnWhenCorrectDateTimeRangeParsed() throws ParseException {
		Date startDate = parseDateTimeRanges(METRIC_UNKNOW_START_DATETIME);
		Date endDate = parseDateTimeRanges(METRIC_UNKNOW_END_DATETIME);
		when(metricsDAO.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category, EventAction.CREATED))
				.thenReturn(0);

		int result = metricsService.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category,
				EventAction.CREATED);
		assertThat(result, Matchers.equalTo(0));
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldNotReturnWhenCorrectDateTimeRangeParsed()
			throws ParseException {
		Date startDate = parseDateRanges(METRIC_KNOWN_END_DATETIME);
		Date endDate = parseDateRanges(METRIC_KNOWN_END_DATETIME);
		when(metricsDAO.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category, EventAction.CREATED))
				.thenReturn(1);

		int result = metricsService.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate, sample_category,
				EventAction.CREATED);
		assertThat(result, Matchers.equalTo(1));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnWhenCorrectDateRangeParsed() throws ParseException {
		Date startDate = parseDateRanges(METRIC_KNOWN_START_DATE);
		Date endDate = parseDateRanges(METRIC_KNOWN_END_DATE);

		encounterMap.put("Consultation", 1);
		encounterMap.put("Hosptialized", 1);

		when(metricsDAO.getEncounterObjectTypesCountByGivenDateRange(startDate, endDate)).thenReturn(encounterMap);

		Map<String, Integer> newEncountersDuringThePeriod = metricsService.getEncounterObjectTypesCountByGivenDateRange(
				startDate, endDate);
		assertThat(newEncountersDuringThePeriod, CoreMatchers.notNullValue());
		assertThat(newEncountersDuringThePeriod.keySet().size(), Matchers.equalTo(2));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnWhenCorrectDateTimeRangeParsed()
			throws ParseException {
		Date startDate = parseDateTimeRanges(METRIC_KNOWN_START_DATETIME);
		Date endDate = parseDateTimeRanges(METRIC_KNOWN_END_DATETIME);

		encounterMap.put("Consultation", 1);
		encounterMap.put("Hosptialized", 1);

		when(metricsDAO.getEncounterObjectTypesCountByGivenDateRange(startDate, endDate)).thenReturn(encounterMap);

		Map<String, Integer> newEncountersDuringThePeriod = metricsService.getEncounterObjectTypesCountByGivenDateRange(
				startDate, endDate);
		assertThat(newEncountersDuringThePeriod, CoreMatchers.notNullValue());
		assertThat(newEncountersDuringThePeriod.keySet().size(), Matchers.equalTo(2));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnNoneWhenIncorrectDateRangeParsed()
			throws ParseException {
		Date startDate = parseDateRanges(METRIC_UNKNOW_START_DATE);
		Date endDate = parseDateRanges(METRIC_UNKNOW_END_DATE);

		when(metricsDAO.getEncounterObjectTypesCountByGivenDateRange(startDate, endDate)).thenReturn(encounterMap);

		Map<String, Integer> newEncountersDuringThePeriod = metricsService.getEncounterObjectTypesCountByGivenDateRange(
				startDate, endDate);
		assertThat(newEncountersDuringThePeriod.size(), Matchers.equalTo(0));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnNoneWhenIncorrectDateTimeRangeParsed()
			throws ParseException {
		Date startDate = parseDateTimeRanges(METRIC_UNKNOW_START_DATETIME);
		Date endDate = parseDateTimeRanges(METRIC_UNKNOW_END_DATETIME);

		when(metricsDAO.getEncounterObjectTypesCountByGivenDateRange(startDate, endDate)).thenReturn(encounterMap);

		Map<String, Integer> newEncountersDuringThePeriod = metricsService.getEncounterObjectTypesCountByGivenDateRange(
				startDate, endDate);
		assertThat(newEncountersDuringThePeriod.size(), Matchers.equalTo(0));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountTodayByEventAction_shouldReturnWhenObjectsExists(){
		when(metricsDAO.fetchTotalOpenMRSObjectCountTodayByEventAction(Patient.class, EventAction.CREATED)).thenReturn(1);

		Integer newPatientsObjectsByGivenDateRange = metricsDAO.fetchTotalOpenMRSObjectCountTodayByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountThisWeekByEventAction_shouldReturnWhenObjectsExists(){
		when(metricsDAO.fetchTotalOpenMRSObjectCountThisWeekByEventAction(Patient.class, EventAction.CREATED)).thenReturn(1);

		Integer newPatientsObjectsByGivenDateRange = metricsDAO.fetchTotalOpenMRSObjectCountThisWeekByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountLastWeekByEventAction_shouldReturnWhenObjectsExists(){
		when(metricsDAO.fetchTotalOpenMRSObjectCountLastWeekByEventAction(Patient.class, EventAction.CREATED)).thenReturn(1);

		Integer newPatientsObjectsByGivenDateRange = metricsDAO.fetchTotalOpenMRSObjectCountLastWeekByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountThisMonthByEventAction_shouldReturnWhenObjectsExists(){
		when(metricsDAO.fetchTotalOpenMRSObjectCountThisMonthByEventAction(Patient.class, EventAction.CREATED)).thenReturn(1);

		Integer newPatientsObjectsByGivenDateRange = metricsDAO.fetchTotalOpenMRSObjectCountThisMonthByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountLastMonthByEventAction_shouldReturnWhenObjectsExists(){
		when(metricsDAO.fetchTotalOpenMRSObjectCountLastMonthByEventAction(Patient.class, EventAction.CREATED)).thenReturn(1);

		Integer newPatientsObjectsByGivenDateRange = metricsDAO.fetchTotalOpenMRSObjectCountLastMonthByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountThisYearByEventAction_shouldReturnWhenObjectsExists(){
		when(metricsDAO.fetchTotalOpenMRSObjectCountThisYearByEventAction(Patient.class, EventAction.CREATED)).thenReturn(1);

		Integer newPatientsObjectsByGivenDateRange = metricsDAO.fetchTotalOpenMRSObjectCountThisYearByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountLastYearByEventAction_shouldReturnWhenObjectsExists(){
		when(metricsDAO.fetchTotalOpenMRSObjectCountLastYearByEventAction(Patient.class, EventAction.CREATED)).thenReturn(1);

		Integer newPatientsObjectsByGivenDateRange = metricsDAO.fetchTotalOpenMRSObjectCountLastYearByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	public Date parseDateRanges(String date) throws ParseException {
		return simpleDateFormatWithoutTime.parse(date);
	}

	public Date parseDateTimeRanges(String date) throws ParseException {
		return simpleDateFormatWithTime.parse(date);
	}
}
