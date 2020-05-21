package org.openmrs.module.metrics.api.db.hibernate;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.Work;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.db.MetricsDAO;

public class HibernateMetricsDAO implements MetricsDAO {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private DbSessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public MetricEvent saveMetricEvent(MetricEvent metricEvent) {
		sessionFactory.getCurrentSession().saveOrUpdate(metricEvent);
		return metricEvent;
	}
	
	@Override
	public Integer getEncounterObjectsByGivenDateRangeAndType(LocalDateTime startRange, LocalDateTime endRange,
	        String encounterType) {
		String sql = "select count(*) "
		        + "from event_records e "
		        + "inner join encounter b on e.object_uuid = b.uuid "
		        + "inner join encounter_type c on b.encounter_type = c.encounter_type_id "
		        + "where e.title = 'Encounter' and (e.date_created between :startRange and :endRange) and e.tags = 'CREATED' "
		        + "and c.name = :encounterType";
		
		return (Integer) sessionFactory.getCurrentSession().createSQLQuery(sql)
		        .setDate("startRange", Date.from(startRange.atZone(ZoneId.systemDefault()).toInstant()))
		        .setDate("endRange", Date.from(endRange.atZone(ZoneId.systemDefault()).toInstant()))
		        .setString("encounterType", encounterType).uniqueResult();
	}
	
	@Override
	public Integer getNewPatientsObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange) {
		String sql = "select count(*)" + "from event_records e"
		        + "where e.title='Patient' and (e.date_created between :startRange and :endRange) and e.tags = 'CREATED'";
		
		return (Integer) sessionFactory.getCurrentSession().createSQLQuery(sql)
		        .setDate("startRange", Date.from(startRange.atZone(ZoneId.systemDefault()).toInstant()))
		        .setDate("endRange", Date.from(endRange.atZone(ZoneId.systemDefault()).toInstant())).uniqueResult();
	}
	
	@Override
	public Map<String, Integer> getEncounterObjectTypesCountByGivenDateRange(LocalDateTime startRange,
			LocalDateTime endRange) {
		Map<String, Integer> encounterObs = new HashMap<>();
		String sql = "select c.name , count(*)" +
				"from event_records e " +
				"inner join encounter b on e.object_uuid = b.uuid " +
				"inner join encounter_type c on b.encounter_type = c.encounter_type_id" +
				"where e.title = 'Encounter' and (e.date_created between :startRange and :endRange) and e.tags = 'CREATED'" +
				"Group BY c.name;";

		final List<Object[]> objs  = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.setDate("startRange", Date.from(startRange.atZone(ZoneId.systemDefault()).toInstant()))
				.setDate("endRange", Date.from(endRange.atZone(ZoneId.systemDefault()).toInstant()))
				.list();
		for (Object[] obj : objs){
			encounterObs.put((String) obj[0],(Integer) obj[1]);
		}
		return encounterObs;
	}
}
