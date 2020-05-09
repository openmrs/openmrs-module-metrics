package org.openmrs.module.metrics.api.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
}
