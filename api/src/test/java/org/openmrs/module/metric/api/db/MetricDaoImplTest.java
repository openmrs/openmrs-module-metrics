///*
// * This Source Code Form is subject to the terms of the Mozilla Public License,
// * v. 2.0. If a copy of the MPL was not distributed with this file, You can
// * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
// * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
// *
// * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
// * graphic logo is a trademark of OpenMRS Inc.
// */
//package org.openmrs.module.metric.api.db;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.notNullValue;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.openmrs.module.metric.TestMetricSpringConfiguration;
//import org.openmrs.module.metrics.MetricEvent;
//import org.openmrs.module.metrics.api.db.hibernate.HibernateMetricsDAO;
//import org.openmrs.test.BaseModuleContextSensitiveTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//
//@ContextConfiguration(classes = TestMetricSpringConfiguration.class, inheritLocations = false)
//public class MetricDaoImplTest extends BaseModuleContextSensitiveTest {
//
//	private static final String METRIC_UUID = "5d6da0f0-44f3-44df-a0bd-b7f36ab4ab65";
//
//	private static final String BAD_PATIENT_UUID = "282390a6-3608-496d-9025-aecbc1235670";
//
//	private static final String METRIC_INITIAL_DATA_XML = "org/openmrs/module/metric/api/db/MetricDaoImplTest_initial_data.xml";
//
//	@Autowired
//	private HibernateMetricsDAO dao;
//
//	@Before
//	public void setup() throws Exception {
//		dao = new HibernateMetricsDAO();
//		executeDataSet(METRIC_INITIAL_DATA_XML);
//	}
//
//	@Test
//	public void saveAllergy_shouldSaveAllergyCorrectly() {
//		MetricEvent existing = dao.getMetricEventByUuid(METRIC_UUID);
//		MetricEvent newMetricEvent = new MetricEvent(existing.getTitle(), existing.getTimeStamp(), existing.getObjectUuid(),
//		        existing.getSerializedContents(), existing.getCategory(), existing.getTags());
//
//		newMetricEvent.setUuid(BAD_PATIENT_UUID);
//		MetricEvent result = dao.saveMetricEvent(newMetricEvent);
//		assertThat(result, notNullValue());
//		assertThat(result.getUuid(), equalTo(BAD_PATIENT_UUID));
//	}
//}
