package org.openmrs.module.mclinic.api.db.hibernate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mclinic.api.MclinicXform;
import org.openmrs.module.mclinic.api.ProgramConfiguration;
import org.openmrs.module.mclinic.api.XformsError;
import org.openmrs.module.mclinic.api.db.MclinicDAO;

import java.util.Collection;
import java.util.List;

/**
 * It is a default implementation of  {@link org.openmrs.module.mclinic.api.db.MclinicDAO}.
 */
public class HibernateMclinicDAO implements MclinicDAO {

	protected final Log log = LogFactory.getLog(HibernateMclinicDAO.class);

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
	 * @see org.openmrs.module.mclinic.api.service.MclinicService#getCohortPatients(org.openmrs.Cohort)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Patient> getCohortPatients(final Cohort cohort) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class);
		criteria.add(Restrictions.in("patientId", cohort.getMemberIds()));
		criteria.add(Restrictions.eq("voided", Boolean.FALSE));
		return criteria.list();
	}

	/**
	 * @see org.openmrs.module.mclinic.api.service.MclinicService#getCohortObservations(org.openmrs.Cohort, java.util.Collection
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Obs> getCohortObservations(final Cohort cohort, final Collection<Concept> concepts) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
		criteria.add(Restrictions.in("personId", cohort.getMemberIds()));
		// only put the concepts restriction when they are not empty. otherwise, just return all obs
		if (CollectionUtils.isNotEmpty(concepts))
			criteria.add(Restrictions.in("concept", concepts));
		criteria.add(Restrictions.eq("voided", Boolean.FALSE));
		return criteria.list();
	}

	/**
	 * @see org.openmrs.module.mclinic.api.service.MclinicService#saveProgramConfiguration(org.openmrs.module.mclinic.api.ProgramConfiguration)
	 */
	@Override
	public ProgramConfiguration saveProgramConfiguration(final ProgramConfiguration programConfiguration) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(programConfiguration);
		return programConfiguration;
	}

	/**
	 * @see org.openmrs.module.mclinic.api.service.MclinicService#getProgramConfiguration(Integer)
	 */
	@Override
	public ProgramConfiguration getProgramConfiguration(final Integer id) throws DAOException {
		return (ProgramConfiguration) sessionFactory.getCurrentSession().get(ProgramConfiguration.class, id);
	}

	/**
	 * @see org.openmrs.module.mclinic.api.service.MclinicService#getProgramConfigurationByUuid(String)
	 */
	@Override
	public ProgramConfiguration getProgramConfigurationByUuid(final String uuid) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProgramConfiguration.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		criteria.add(Restrictions.eq("retired", Boolean.FALSE));
		return (ProgramConfiguration) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.module.mclinic.api.service.MclinicService#getProgramConfigurations()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ProgramConfiguration> getProgramConfigurations() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProgramConfiguration.class);
		criteria.add(Restrictions.eq("retired", Boolean.FALSE));
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.module.mclinic.api.db.MclinicDAO#saveErrorInDatabase(org.openmrs.module.mclinic.api.XformsError)
	 */
	public void saveErrorInDatabase(XformsError xformsError) {
		sessionFactory.getCurrentSession().saveOrUpdate(xformsError);
	}

	/**
	 * @see org.openmrs.module.mclinic.api.db.MclinicDAO#getAllXformsErrors()
	 */
	@SuppressWarnings("unchecked")
	public List<XformsError> getAllXformsErrors() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(XformsError.class);
		return (List<XformsError>) criteria.list();
	}

	/**
	 * @see org.openmrs.module.mclinic.api.db.MclinicDAO#getErrorById(java.lang.Integer)
	 */
	public XformsError getErrorById(Integer errorId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(XformsError.class);
		criteria.add(Expression.like("xformsErrorId", errorId));
		return (XformsError) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.module.mclinic.api.db.MclinicDAO#deleteError(org.openmrs.module.mclinic.api.XformsError)
	 */
	public void deleteError(XformsError error) {
		sessionFactory.getCurrentSession().delete(error);
	}
	
	/**
	 * @see org.openmrs.module.mclinic.api.db.MclinicDAO#getAllDownloadableXforms()
	 */
	@SuppressWarnings("unchecked")
	public List<MclinicXform> getAllDownloadableXforms() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MclinicXform.class);
		return (List<MclinicXform>) criteria.list();
	}

	@Override
	public void saveDownloadableXform(MclinicXform xform) {
		sessionFactory.getCurrentSession().saveOrUpdate(xform);
	}

	@Override
	public MclinicXform getDownloadableXform(Integer mclinicXformId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MclinicXform.class);
		criteria.add(Expression.like("mclinicXformId", mclinicXformId));
		return (MclinicXform) criteria.uniqueResult();
	}

	@Override
	public MclinicXform getDownloadableXformByFormId(Integer xformId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MclinicXform.class);
		criteria.add(Expression.like("xformId", xformId));
		return (MclinicXform) criteria.uniqueResult();
	}
	
	@Override
	public void deleteDownloadableXform(MclinicXform mclinicXform) {
		sessionFactory.getCurrentSession().delete(mclinicXform);
	}

	@Override
	public MclinicXform getDownloadableXformByName(String formName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MclinicXform.class);
		criteria.add(Expression.like("xformName", formName));
		return (MclinicXform) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<XformsError> getErrorsByFormName(String formName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(XformsError.class);
		criteria.add(Expression.like("formName", formName));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MclinicXform> getDownloadableXformsByProgram(ProgramConfiguration program) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MclinicXform.class);
		criteria.add(Expression.like("program", program));
		return criteria.list();
	}
}
