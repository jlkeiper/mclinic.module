package org.openmrs.module.mclinic.api.service;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mclinic.api.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p/>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(MclinicService.class).someMethod();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface MclinicService extends OpenmrsService {

	/**
	 * Service method to get all patients inside the cohort
	 *
	 * @param cohort the cohort
	 * @return all patients in the cohort or empty list when no patient match the patient id in the cohort
	 * @throws org.openmrs.api.APIException when the process failed
	 */
	@Transactional(readOnly = true)
	List<Patient> getCohortPatients(final Cohort cohort) throws APIException;

	/**
	 * Service method to get all observations for all patients in the cohort
	 *
	 *
	 * @param cohort   the cohort
	 * @param concepts the concepts
	 * @return all observations for patients in the cohort or empty list when no observations for the patient ids in the cohort exists
	 * @throws org.openmrs.api.APIException when the process failed
	 */
	@Transactional(readOnly = true)
	List<Obs> getCohortObservations(final Cohort cohort, final Collection<Concept> concepts) throws APIException;

	/**
	 * Service method to save the concept configuration to the database
	 *
	 * @param programConfiguration the concept configuration
	 * @return saved concept configuration
	 * @throws APIException when saving failed
	 */
	@Transactional
	ProgramConfiguration saveProgramConfiguration(final ProgramConfiguration programConfiguration) throws APIException;

	/**
	 * Get concept configuration based on the configuration id
	 *
	 * @param id the concept configuration id
	 * @return the matching concept configuration or null if no matching record found in the database
	 * @throws APIException when fetching failed
	 */
	@Transactional(readOnly = true)
	ProgramConfiguration getProgramConfiguration(final Integer id) throws APIException;

	/**
	 * Get concept configuration based on the configuration uuid
	 *
	 * @param uuid the concept configuration uuid
	 * @return the matching concept configuration or null if no matching record found in the database
	 * @throws APIException when fetching failed
	 */
	@Transactional(readOnly = true)
	ProgramConfiguration getProgramConfigurationByUuid(final String uuid) throws APIException;

	/**
	 * Get all saved concept configuration
	 *
	 * @return all saved concept configuration or empty list when there's no saved concept configuration
	 * @throws APIException when fetching failed
	 */
	@Transactional(readOnly = true)
	List<ProgramConfiguration> getProgramConfigurations() throws APIException;
	
	/**
	 * A getter for all downloadable forms uploaded into the module
	 * @return List of resource files
	 */
	public List<MclinicXform> getAllDownloadableXforms();
	
	/**
	 * Get an xform queue given the absolute path to an xml file
	 * @param absoluteFilePath
	 * @return {@link XformsQueue}
	 */
	public XformsQueue getMclinicQueue(String absoluteFilePath);

	/**
	 * Get all errors logged in the error table
	 * @return list of {@link XformsError}
	 */
	public List<XformsError> getAllXformsErrors();
	
	/**
	 * Get a specific  error by a specified id
	 * @param errorId
	 * @return {@link XformsError}
	 */
	public XformsError getErrorById(Integer errorId);
	
	/**
	 * Create a new error record in the error table
	 * @param mclinicError
	 */
	public void saveErrorInDatabase(XformsError mclinicError);
	
	/** Delete a specified error from the database
	 * @param error
	 */
	public void deleteError(XformsError error);

	/**
	 * Generate sync logs for a specific date. If no logs return null
	 * @param logDate
	 * @return List of sync logs
	 */
	public List<SyncLogModel> getSyncLog(Date logDate);
	
	/**
	 * Get all date encoded sync log files. If none return null
	 * @return List of sync log files
	 */
	public List<String> getAllSyncLogs();
	
	public SortedMap<String, String> getSystemVariables();

	public void saveDownloadableXform(MclinicXform xform);

	public MclinicXform getDownloadableXform(Integer mclinicXformId);
	
	public MclinicXform getDownloadableXformByFormId(Integer xformId);

	public void deleteDownloadableXform(MclinicXform mclinicXform);

	public MclinicXform getDownloadableXformByName(String formName);

	List<MclinicXform> getDownloadableXformsByProgram(ProgramConfiguration program);
}
