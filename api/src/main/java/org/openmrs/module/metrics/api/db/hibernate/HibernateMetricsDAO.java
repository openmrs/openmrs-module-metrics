package org.openmrs.module.metrics.api.db.hibernate;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.between;

import static org.hibernate.criterion.Restrictions.ge;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getCategoryNameFromClass;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastMonthEndDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastMonthStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastWeekEndDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastWeekStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastYearEndDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getLastYearStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisMonthEndDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisMonthStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisWeekEndDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisWeekStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisYearEndDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getThisYearStartDate;
import static org.openmrs.module.metrics.api.utils.EventsUtils.getToday;

import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.EventAction;
import org.openmrs.module.metrics.api.db.MetricsDAO;

public class HibernateMetricsDAO implements MetricsDAO {

	private DbSessionFactory sessionFactory;

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public MetricEvent getMetricEventByUuid(String uuid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetricEvent.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (MetricEvent) criteria.uniqueResult();
	}

	@Override
	public MetricEvent saveMetricEvent(MetricEvent metricEvent) {
		sessionFactory.getCurrentSession().saveOrUpdate(metricEvent);
		return metricEvent;
	}

	@Override
	public int getEncounterObjectsByGivenDateRangeAndType(Date startRange, Date endRange, String encounterType) {
		// @formatter:off
		String sql = "select count(*) "
				+ "from metric_event_records e "
				+ "inner join encounter b on e.object_uuid = b.uuid "
				+ "inner join encounter_type c on b.encounter_type = c.encounter_type_id "
				+ "where e.title = 'Encounter' and (e.date_created between :startRange and :endRange) and e.tags = 'CREATED' "
				+ "and c.name = :encounterType";
		// @formatter:on

		return (int) sessionFactory.getCurrentSession().createSQLQuery(sql).setParameter("startRange", startRange)
				.setParameter("endRange", endRange).setParameter("encounterType", encounterType).uniqueResult();
	}

	@Override
	public int getNewObjectsByGivenDateRangeAndCategory(Date startRange, Date endRange, String category, EventAction action) {

		return ((Long) sessionFactory.getCurrentSession().createCriteria(MetricEvent.class)
				.add(eq("category", category))
				.add(between("timeStamp", startRange, endRange))
				.add(eq("tags", action.toString()))
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(Date startRange,
			Date endRange) {
		Map<String, Integer> encounterObs = new HashMap<>();
		// @formatter:off
		String sql = "select c.name , count(*) "
				+ "from metric_event_records e "
				+ "inner join encounter b on e.object_uuid = b.uuid "
				+ "inner join encounter_type c on b.encounter_type = c.encounter_type_id "
				+ "where e.title = 'Encounter' and (e.date_created between :startRange and :endRange) and e.tags = 'CREATED'"
				+ "group by c.name;";
		// @formatter:on

		final List<Object[]> objs = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.setParameter("startRange", startRange)
				.setParameter("endRange", endRange)
				.list();

		for (Object[] obj : objs) {
			encounterObs.put((String) obj[0], ((BigInteger) obj[1]).intValue());
		}

		return encounterObs;
	}

	@Override
	public int fetchTotalOpenMRSObjectCountTodayByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return ((Long) sessionFactory.getCurrentSession().createCriteria(MetricEvent.class)
				.add(eq("category", getCategoryNameFromClass(clazz)))
				.add(ge("timeStamp", Date.from(getToday().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
				.add(eq("tags", action.toString())).setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public int fetchTotalOpenMRSObjectCountThisWeekByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return getNewObjectsByGivenDateRangeAndCategory(getThisWeekStartDate(), getThisWeekEndDate(),
				getCategoryNameFromClass(clazz), action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountLastWeekByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return getNewObjectsByGivenDateRangeAndCategory(getLastWeekStartDate(), getLastWeekEndDate(),
				getCategoryNameFromClass(clazz), action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountLastMonthByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return getNewObjectsByGivenDateRangeAndCategory(getLastMonthStartDate(), getLastMonthEndDate(),
				getCategoryNameFromClass(clazz), action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountThisMonthByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return getNewObjectsByGivenDateRangeAndCategory(getThisMonthStartDate(), getThisMonthEndDate(),
				getCategoryNameFromClass(clazz), action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountThisYearByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return getNewObjectsByGivenDateRangeAndCategory(getThisYearStartDate(), getThisYearEndDate(),
				getCategoryNameFromClass(clazz), action);
	}

	@Override
	public int fetchTotalOpenMRSObjectCountLastYearByEventAction(Class<? extends OpenmrsObject> clazz, EventAction action) {
		return getNewObjectsByGivenDateRangeAndCategory(getLastYearStartDate(), getLastYearEndDate(),
				getCategoryNameFromClass(clazz), action);
	}
}
