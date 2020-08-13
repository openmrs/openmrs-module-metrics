/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.metric.api.dao.impl;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastMonthStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastWeekStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastYearStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisMonthStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisYearStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getToday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import net.sourceforge.jtds.jdbc.DateTime;
import org.hamcrest.CoreMatchers;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Allergy;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metric.TestMetricSpringConfiguration;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.EventAction;
import org.openmrs.module.metrics.api.db.hibernate.HibernateMetricsDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestMetricSpringConfiguration.class, inheritLocations = false)
public class MetricDaoImplTest extends BaseModuleContextSensitiveTest {

	private static final String METRIC_UUID = "5d6da0f0-44f3-44df-a0bd-b7f36ab4ab65";

	private static final String NEW_METRIC_UUID = "282390a6-3608-496d-9025-aecbc1235670";

	private static final String UNKNOWN_METRIC_UUID = "cvdasdsa-3608-496d-9025-asafasfas";

	private static final String METRIC_INITIAL_DATA_XML = "org/openmrs/module/metric/api/db/MetricDaoImplTest_initial_data.xml";

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

	private HibernateMetricsDAO dao;

	MetricEvent metricEvent = null;

	@Autowired
	@Qualifier("dbSessionFactory")
	private DbSessionFactory sessionFactory;

	@Before
	public void setup() throws Exception {
		dao = new HibernateMetricsDAO();
		dao.setSessionFactory(sessionFactory);
		executeDataSet(METRIC_INITIAL_DATA_XML);

	}

	@Test
	public void saveMetricEvent_shouldSaveMetricEventWithNewUUID() {
		MetricEvent existing = dao.getMetricEventByUuid(METRIC_UUID);
		MetricEvent newMetricEvent = new MetricEvent(existing.getTitle(), existing.getTimeStamp(), existing.getObjectUuid(),
				existing.getSerializedContents(), existing.getCategory(), existing.getTags());

		newMetricEvent.setUuid(NEW_METRIC_UUID);
		MetricEvent result = dao.saveMetricEvent(newMetricEvent);
		assertThat(result, notNullValue());
		assertThat(result.getUuid(), equalTo(NEW_METRIC_UUID));
	}

	@Test
	public void getMetricEventByUuid_shouldGetByUuid() {
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		assertThat(metricEvent, CoreMatchers.notNullValue());
		assertThat(metricEvent.getUuid(), CoreMatchers.notNullValue());
		assertThat(metricEvent.getUuid(), CoreMatchers.equalTo(METRIC_UUID));
	}

	@Test
	public void getMetricEventByUuid_shouldReturnNullWhenCalledWithUnknownUuid() {
		MetricEvent metricEvent = dao.getMetricEventByUuid(UNKNOWN_METRIC_UUID);
		assertThat(metricEvent, nullValue());
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldReturnWhenCorrectDateRangeParsed() throws ParseException {
		Date startDate = parseDateRanges(METRIC_KNOWN_START_DATE);
		Date endDate = parseDateRanges(METRIC_KNOWN_END_DATE);
		Integer newPatientsObjectsByGivenDateRange = dao.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate,
				sample_category, EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldReturnWhenCorrectDateTimeRangeParsed() throws ParseException {
		Date startDate = parseDateTimeRanges(METRIC_KNOWN_START_DATETIME);
		Date endDate = parseDateTimeRanges(METRIC_KNOWN_END_DATETIME);
		Integer newPatientsObjectsByGivenDateRange = dao.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate,
				sample_category, EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldReturnNoneWhenIncorrectDateRangeParsed()
			throws ParseException {
		Date startDate = parseDateRanges(METRIC_UNKNOW_START_DATE);
		Date endDate = parseDateRanges(METRIC_UNKNOW_END_DATE);
		Integer newPatientsObjectsByGivenDateRange = dao.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate,
				sample_category, EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(0));
	}

	@Test
	public void getNewObjectsByGivenDateRangeAndCategory_shouldReturnNoneWhenIncorrectDateTimeRangeParsed()
			throws ParseException {
		Date startDate = parseDateTimeRanges(METRIC_UNKNOW_START_DATETIME);
		Date endDate = parseDateTimeRanges(METRIC_UNKNOW_END_DATETIME);
		Integer newPatientsObjectsByGivenDateRange = dao.getNewObjectsByGivenDateRangeAndCategory(startDate, endDate,
				sample_category, EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(0));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnWhenCorrectDateRangeParsed() throws ParseException {
		Date startDate = parseDateRanges(METRIC_KNOWN_START_DATE);
		Date endDate = parseDateRanges(METRIC_KNOWN_END_DATE);
		Map<String, Integer> newEncountersDuringThePeriod = dao.getEncounterObjectTypesCountByGivenDateRange(startDate,
				endDate);
		assertThat(newEncountersDuringThePeriod, CoreMatchers.notNullValue());
		assertThat(newEncountersDuringThePeriod.keySet().size(), equalTo(2));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnWhenCorrectDateTimeRangeParsed()
			throws ParseException {
		Date startDate = parseDateTimeRanges(METRIC_KNOWN_START_DATETIME);
		Date endDate = parseDateTimeRanges(METRIC_KNOWN_END_DATETIME);
		Map<String, Integer> newEncountersDuringThePeriod = dao.getEncounterObjectTypesCountByGivenDateRange(startDate,
				endDate);
		assertThat(newEncountersDuringThePeriod, CoreMatchers.notNullValue());
		assertThat(newEncountersDuringThePeriod.keySet().size(), equalTo(2));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnNoneWhenIncorrectDateRangeParsed()
			throws ParseException {
		Date startDate = parseDateRanges(METRIC_UNKNOW_START_DATE);
		Date endDate = parseDateRanges(METRIC_UNKNOW_END_DATE);
		Map<String, Integer> newEncountersDuringThePeriod = dao.getEncounterObjectTypesCountByGivenDateRange(startDate,
				endDate);
		assertThat(newEncountersDuringThePeriod.size(), equalTo(0));
	}

	@Test
	public void getEncounterObjectTypesCountByGivenDateRange_shouldReturnNoneWhenIncorrectDateTimeRangeParsed()
			throws ParseException {
		Date startDate = parseDateTimeRanges(METRIC_UNKNOW_START_DATETIME);
		Date endDate = parseDateTimeRanges(METRIC_UNKNOW_END_DATETIME);
		Map<String, Integer> newEncountersDuringThePeriod = dao.getEncounterObjectTypesCountByGivenDateRange(startDate,
				endDate);
		assertThat(newEncountersDuringThePeriod.size(), equalTo(0));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountTodayByEventAction_shouldReturnWhenObjectsExists(){
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		metricEvent.setTimeStamp(Date.from(getToday().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		dao.saveMetricEvent(metricEvent);

		Integer newPatientsObjectsByGivenDateRange = dao.fetchTotalOpenMRSObjectCountTodayByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountThisWeekByEventAction_shouldReturnWhenObjectsExists(){
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		metricEvent.setTimeStamp(Date.from(getToday().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		dao.saveMetricEvent(metricEvent);

		Integer newPatientsObjectsByGivenDateRange = dao.fetchTotalOpenMRSObjectCountThisWeekByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountLastWeekByEventAction_shouldReturnWhenObjectsExists(){
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		metricEvent.setTimeStamp(getLastWeekStartDate());
		dao.saveMetricEvent(metricEvent);

		Integer newPatientsObjectsByGivenDateRange = dao.fetchTotalOpenMRSObjectCountLastWeekByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountThisMonthByEventAction_shouldReturnWhenObjectsExists(){
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		metricEvent.setTimeStamp(getThisMonthStartDate());
		dao.saveMetricEvent(metricEvent);

		Integer newPatientsObjectsByGivenDateRange = dao.fetchTotalOpenMRSObjectCountThisMonthByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountLastMonthByEventAction_shouldReturnWhenObjectsExists(){
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		metricEvent.setTimeStamp(getLastMonthStartDate());
		dao.saveMetricEvent(metricEvent);

		Integer newPatientsObjectsByGivenDateRange = dao.fetchTotalOpenMRSObjectCountLastMonthByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountThisYearByEventAction_shouldReturnWhenObjectsExists(){
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		metricEvent.setTimeStamp(getThisYearStartDate());
		dao.saveMetricEvent(metricEvent);

		Integer newPatientsObjectsByGivenDateRange = dao.fetchTotalOpenMRSObjectCountThisYearByEventAction(Patient.class,
				EventAction.CREATED);
		assertThat(newPatientsObjectsByGivenDateRange, equalTo(1));
	}

	@Test
	public void fetchTotalOpenMRSObjectCountLastYearByEventAction_shouldReturnWhenObjectsExists(){
		metricEvent = dao.getMetricEventByUuid(METRIC_UUID);
		metricEvent.setTimeStamp(getLastYearStartDate());
		dao.saveMetricEvent(metricEvent);

		Integer newPatientsObjectsByGivenDateRange = dao.fetchTotalOpenMRSObjectCountLastYearByEventAction(Patient.class,
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
