package org.openmrs.module.metrics.api.db.hibernate;

import static org.hibernate.criterion.Restrictions.eq;

import javax.persistence.criteria.JoinType;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	public Integer getEncounterObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange, String encounterType) {
		String sql = "SELECT count(*)"
		        + "FROM server4.event_records e inner join server4.encounter b on e.uri = b.uuid inner join server4.encounter_type"
		        + " c on b.encounter_type = c.encounter_type_id" + "WHERE e.title='Encounter' and (e.date_created BETWEEN "
		        + startRange + " and " + endRange + ") and e.tags = 'CREATED' " + "and c.name = " + encounterType + "";
		
		Integer count = ((BigInteger) sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult()).intValue();
		return count;
	}
	
	@Override
	public Integer getNewPatientsObjectsByGivenDateRange(LocalDateTime startRange, LocalDateTime endRange) {
		String sql = "SELECT count(*)"
		        + "FROM server4.event_records e"
		        + "WHERE e.title='Patient' and (e.date_created BETWEEN '2020-05-08 22:32:26.000' and '2020-05-16 22:32:26.000') and e.tags = 'CREATED'";
		
		Integer count = ((BigInteger) sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult()).intValue();
		return count;
	}
	
}
