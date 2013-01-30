package org.openmrs.module.mclinic.api.db;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mclinic.api.MclinicXform;
import org.openmrs.module.mclinic.api.ProgramConfiguration;
import org.openmrs.module.mclinic.api.XformsError;

import java.util.Collection;
import java.util.List;

/**
 * Database methods for {@link org.openmrs.module.mclinic.api.service.MclinicService}.
 */
public interface MclinicDAO {

	/*
		 * Add DAO methods here
		 */

	/**
	 * DAO method to get all patients inside the cohort
	 *
	 * @param cohort the cohort
	 * @return all patients in the cohort or empty list when no patient match the patient id in the cohort
	 * @throws org.openmrs.api.db.DAOException
	 *          when the process failed
	 */
	List<Patient> getCohortPatients(final Cohort cohort) throws DAOException;

	/**
	 * DAO method to get all observations for all patients in the cohort
	 *
	 *
	 * @param cohort   the cohort
	 * @param concepts the concepts
	 * @return all observations for patients in the cohort or empty list when no observations for the patient ids in the cohort exists
	 * @throws org.openmrs.api.db.DAOException
	 *          when the process failed
	 */
	List<Obs> getCohortObservations(final Cohort cohort, final Collection<Concept> concepts) throws DAOException;

	/**
	 * DAO method to save the concept configuration to the database
	 *
	 * @param programConfiguration the concept configuration
	 * @return saved concept configuration
	 * @throws org.openmrs.api.db.DAOException
	 *          when saving failed
	 */
	ProgramConfiguration saveProgramConfiguration(final ProgramConfiguration programConfiguration) throws DAOException;

	/**
	 * Get concept configuration based on the configuration id
	 *
	 *
	 * @param id the concept configuration id
	 * @return the matching concept configuration or null if no matching record found in the database
	 * @throws org.openmrs.api.db.DAOException
	 *          when fetching failed
	 */
	ProgramConfiguration getProgramConfiguration(final Integer id) throws DAOException;

	/**
	 * Get concept configuration based on the configuration uuid
	 *
	 * @param uuid the concept configuration id
	 * @return the matching concept configuration or null if no matching record found in the database
	 * @throws org.openmrs.api.db.DAOException
	 *          when fetching failed
	 */
	ProgramConfiguration getProgramConfigurationByUuid(final String uuid) throws DAOException;

	/**
	 * Get all saved concept configuration
	 *
	 * @return all saved concept configuration or empty list when there's no saved concept configuration
	 * @throws org.openmrs.api.APIException when fetching failed
	 */
	List<ProgramConfiguration> getProgramConfigurations();
	
	public void saveErrorInDatabase(XformsError xformsError);

	public List<XformsError> getAllXformsErrors();

	public XformsError getErrorById(Integer errorId);

	public void deleteError(XformsError error);

	public List<MclinicXform> getAllDownloadableXforms();

	public void saveDownloadableXform(MclinicXform xform);

	public MclinicXform getDownloadableXform(Integer mclinicXformId);

	public void deleteDownloadableXform(MclinicXform mclinicXform);

	public MclinicXform getDownloadableXformByFormId(Integer xformId);

	public MclinicXform getDownloadableXformByName(String formName);

	public List<XformsError> getErrorsByFormName(String formName);

	List<MclinicXform> getDownloadableXformsByProgram(ProgramConfiguration program);
}
