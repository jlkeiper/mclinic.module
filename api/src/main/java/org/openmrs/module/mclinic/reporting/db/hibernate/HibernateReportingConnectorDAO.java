package org.openmrs.module.mclinic.reporting.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mclinic.reporting.db.ReportingConnectorDAO;
import org.openmrs.module.mclinic.reporting.metadata.DefinitionProperty;
import org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;

import java.util.List;

public class HibernateReportingConnectorDAO implements ReportingConnectorDAO {

	private SessionFactory sessionFactory;

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @see org.openmrs.module.mclinic.reporting.service.ReportingConnectorService#saveExtendedDefinition(org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition)
	 */
	@Override
	public ExtendedDefinition saveExtendedDefinition(final ExtendedDefinition extendedDefinition) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(extendedDefinition);
		return extendedDefinition;
	}

	/**
	 * @see org.openmrs.module.mclinic.reporting.service.ReportingConnectorService#getExtendedDefinitionByUuid(String)
	 */
	@Override
	public ExtendedDefinition getExtendedDefinitionByUuid(final String uuid) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExtendedDefinition.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		criteria.add(Restrictions.eq("retired", Boolean.FALSE));
		return (ExtendedDefinition) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.module.mclinic.reporting.service.ReportingConnectorService#getExtendedDefinitionByDefinition(org.openmrs.module.reporting.cohort.definition.CohortDefinition)
	 */
	@Override
	public ExtendedDefinition getExtendedDefinitionByDefinition(final CohortDefinition definition) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExtendedDefinition.class);
		criteria.add(Restrictions.eq("cohortDefinition", definition));
		criteria.add(Restrictions.eq("retired", Boolean.FALSE));
		return (ExtendedDefinition) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.module.mclinic.reporting.service.ReportingConnectorService#getExtendedDefinition(Integer)
	 */
	@Override
	public ExtendedDefinition getExtendedDefinition(final Integer id) throws DAOException {
		return (ExtendedDefinition) sessionFactory.getCurrentSession().get(ExtendedDefinition.class, id);
	}

	/**
	 * @see org.openmrs.module.mclinic.reporting.service.ReportingConnectorService#getAllExtendedDefinition()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ExtendedDefinition> getAllExtendedDefinition() throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExtendedDefinition.class);
		criteria.add(Restrictions.eq("retired", Boolean.FALSE));
		return criteria.list();
	}

	/**
	 * @see org.openmrs.module.mclinic.reporting.service.ReportingConnectorService#saveDefinitionProperty(org.openmrs.module.mclinic.reporting.metadata.DefinitionProperty)
	 */
	@Override
	public DefinitionProperty saveDefinitionProperty(final DefinitionProperty definitionProperty) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(definitionProperty);
		return definitionProperty;
	}

	/**
	 * @see org.openmrs.module.mclinic.reporting.service.ReportingConnectorService#getDefinitionProperty(Integer)
	 */
	@Override
	public DefinitionProperty getDefinitionProperty(final Integer id) throws DAOException {
		return (DefinitionProperty) sessionFactory.getCurrentSession().get(DefinitionProperty.class, id);
	}

}
