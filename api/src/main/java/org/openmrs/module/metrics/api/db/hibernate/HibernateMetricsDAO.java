package org.openmrs.module.metrics.api.db.hibernate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.MetricsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HibernateMetricsDAO implements MetricsDAO {
	
	private DbSessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	@Autowired
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
	public Integer getEncounterObjectsByGivenDateRangeAndType(LocalDateTime startRange, LocalDateTime endRange,
	        String encounterType) {
		// @formatter:off
		String sql = "select count(*) "
		        + "from event_records e "
		        + "inner join encounter b on e.object_uuid = b.uuid "
		        + "inner join encounter_type c on b.encounter_type = c.encounter_type_id "
		        + "where e.title = 'Encounter' and (e.date_created between :startRange and :endRange) and e.tags = 'CREATED' "
		        + "and c.name = :encounterType";
		// @formatter:on
		
		return (Integer) sessionFactory.getCurrentSession().createSQLQuery(sql)
		        .setParameter("startRange", Date.from(startRange.atZone(ZoneId.systemDefault()).toInstant()))
		        .setParameter("endRange", Date.from(endRange.atZone(ZoneId.systemDefault()).toInstant()))
		        .setParameter("encounterType", encounterType).uniqueResult();
	}
	
	@Override
	public Integer getNewPatientsObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange) {
		// @formatter:off
		String sql = "select count(*) " + "from event_records e " + "where e.title='Patient' and "
		        + "(e.date_created between :startRange and :endRange) and " + "e.tags = 'CREATED'";
		// @formatter:on
		
		return (Integer) sessionFactory.getCurrentSession().createSQLQuery(sql)
		        .setParameter("startRange", Date.from(startRange.atZone(ZoneId.systemDefault()).toInstant()))
		        .setParameter("endRange", Date.from(endRange.atZone(ZoneId.systemDefault()).toInstant())).uniqueResult();
	}
	
	@Override
	public Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(LocalDateTime startRange,
			LocalDateTime endRange) {
		Map<String, Integer> encounterObs = new HashMap<>();
		// @formatter:off
		String sql = "select c.name , count(*) " +
				"from event_records e " +
				"inner join encounter b on e.object_uuid = b.uuid " +
				"inner join encounter_type c on b.encounter_type = c.encounter_type_id " +
				"where e.title = 'Encounter' and (e.date_created between :startRange and :endRange) and e.tags = 'CREATED'" +
				"group by c.name;";
		// @formatter:on

		final List<Object[]> objs = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.setParameter("startRange", Date.from(startRange.atZone(ZoneId.systemDefault()).toInstant()))
				.setParameter("endRange", Date.from(endRange.atZone(ZoneId.systemDefault()).toInstant()))
				.list();

		for (Object[] obj : objs) {
			encounterObs.put((String) obj[0], (Integer) obj[1]);
		}

		return encounterObs;
	}
}
