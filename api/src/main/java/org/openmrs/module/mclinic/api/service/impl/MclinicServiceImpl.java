package org.openmrs.module.mclinic.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mclinic.api.*;
import org.openmrs.module.mclinic.api.db.MclinicDAO;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * It is a default implementation of {@link org.openmrs.module.mclinic.api.service.MclinicService}.
 */
public class MclinicServiceImpl extends BaseOpenmrsService implements MclinicService {

	protected final Log log = LogFactory.getLog(MclinicServiceImpl.class);

	private MclinicDAO dao;

	/**
	 * @param dao the dao to set
	 */
	public void setMclinicDao(final MclinicDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public MclinicDAO getMclinicDao() {
		return dao;
	}

	/**
	 * Service method to get all patients inside the cohort
	 *
	 * @param cohort the cohort
	 * @return all patients in the cohort or empty list when no patient match the patient id in the cohort
	 * @throws org.openmrs.api.APIException when the process failed
	 */
	@Override
	public List<Patient> getCohortPatients(final Cohort cohort) throws APIException {
        List<Patient> patients = new ArrayList<Patient>();
		if (cohort == null || CollectionUtils.isEmpty(cohort.getMemberIds()))
			return patients;
//
//        PatientService patientService = Context.getPatientService();
//        for (Integer patientId : cohort.getMemberIds())
//            patients.add(patientService.getPatient(patientId));
//
//		return patients;
        return dao.getCohortPatients(cohort);
	}

	/**
	 * Service method to get all observations for all patients in the cohort
	 *
	 *
	 * @param cohort   the cohort
	 * @param concepts the concepts
	 * @return all observations for patients in the cohort or empty list when no observations for the patient ids in the cohort exists
	 * @throws org.openmrs.api.APIException when the process failed
	 */
	@Override
	public List<Obs> getCohortObservations(final Cohort cohort, final Collection<Concept> concepts) throws APIException {
		if (cohort == null || CollectionUtils.isEmpty(cohort.getMemberIds()) || CollectionUtils.isEmpty(concepts))
			return new ArrayList<Obs>();
		return dao.getCohortObservations(cohort, concepts);
	}

	/**
	 * Service method to save the concept configuration to the database
	 *
	 * @param programConfiguration the concept configuration
	 * @return saved concept configuration
	 * @throws org.openmrs.api.APIException when saving failed
	 */
	@Override
	public ProgramConfiguration saveProgramConfiguration(final ProgramConfiguration programConfiguration) throws APIException {
		return dao.saveProgramConfiguration(programConfiguration);
	}

	/**
	 * Get concept configuration based on the configuration id
	 *
	 * @param id the concept configuration id
	 * @return the matching concept configuration or null if no matching record found in the database
	 * @throws org.openmrs.api.APIException when fetching failed
	 */
	@Override
	public ProgramConfiguration getProgramConfiguration(final Integer id) throws APIException {
		return dao.getProgramConfiguration(id);
	}

	/**
	 * Get concept configuration based on the configuration uuid
	 *
	 * @param uuid the concept configuration id
	 * @return the matching concept configuration or null if no matching record found in the database
	 * @throws org.openmrs.api.APIException when fetching failed
	 */
	@Override
	public ProgramConfiguration getProgramConfigurationByUuid(final String uuid) throws APIException {
		return dao.getProgramConfigurationByUuid(uuid);
	}

	/**
	 * Get all saved concept configuration
	 *
	 * @return all saved concept configuration or empty list when there's no saved concept configuration
	 * @throws org.openmrs.api.APIException when fetching failed
	 */
	@Override
	public List<ProgramConfiguration> getProgramConfigurations() throws APIException {
		return dao.getProgramConfigurations();
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getMclinicQueue(java.lang.String)
	 */
	public XformsQueue getMclinicQueue(String absoluteFilePath) {
		XformsQueue queueItem = new XformsQueue();
		queueItem.setFileSystemUrl(absoluteFilePath);
		log.debug(absoluteFilePath);
		return queueItem;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getSystemVariables()
	 */
	public SortedMap<String,String> getSystemVariables() {
		TreeMap<String, String> systemVariables = new TreeMap<String, String>();
		systemVariables.put("XFORMS_DROP_DIR", MclinicUtil.getMclinicDropDir().getAbsolutePath());
		systemVariables.put("XFORMS_QUEUE_DIR", MclinicUtil.getMclinicQueueDir().getAbsolutePath());
		systemVariables.put("XFORMS_ARCHIVE_DIR", MclinicUtil.getMclinicArchiveDir(null).getAbsolutePath());
		systemVariables.put("XFORMS_ERROR_DIR", MclinicUtil.getMclinicErrorDir().getAbsolutePath());
		return systemVariables;
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getAllDownloadableXforms()
	 */
	public List<MclinicXform> getAllDownloadableXforms() {
		return dao.getAllDownloadableXforms();
	}
	

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#saveDownloadableXform(org.openmrs.module.mclinic.MclinicXform)
	 */
	public void saveDownloadableXform(MclinicXform xform) {
		dao.saveDownloadableXform(xform);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#saveErrorInDatabase(org.openmrs.module.mclinic.XformsError)
	 */
	public void saveErrorInDatabase(XformsError xformsError) {
		// first get an error in the database based on similar formName.
		dao.saveErrorInDatabase(xformsError);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getDownloadableXform(java.lang.Integer)
	 */
	public MclinicXform getDownloadableXform(Integer mclinicXformId) {
		return dao.getDownloadableXform(mclinicXformId);
	}
	

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getDownloadableXformByFormId(java.lang.Integer)
	 */
	public MclinicXform getDownloadableXformByFormId(Integer xformId) {
		return dao.getDownloadableXformByFormId(xformId);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getDownloadableXformName(java.lang.String)
	 */
	public MclinicXform getDownloadableXformByName(String formName) {
		return dao.getDownloadableXformByName(formName);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#deleteDownloadableXform(org.openmrs.module.mclinic.MclinicXform)
	 */
	public void deleteDownloadableXform(MclinicXform mclinicXform) {
		dao.deleteDownloadableXform(mclinicXform);
	}
	
	@Override
	public List<MclinicXform> getDownloadableXformsByProgram(ProgramConfiguration program) {
		return dao.getDownloadableXformsByProgram(program);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getAllXformsErrors()
	 */
	public List<XformsError> getAllXformsErrors() {
		return dao.getAllXformsErrors();
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getErrorById(java.lang.Integer)
	 */
	public XformsError getErrorById(Integer errorId) {
		return dao.getErrorById(errorId);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#deleteError(org.openmrs.module.mclinic.XformsError)
	 */
	public void deleteError(XformsError error) {
		dao.deleteError(error);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getSyncLog(java.util.Date)
	 */
	public List<SyncLogModel> getSyncLog(Date logDate) {
		List<SyncLogModel> logList = new ArrayList<SyncLogModel>();
		File logDir=MclinicUtil.getMclinicSyncLogDir();
		if (logDate == null)
			logDate=new Date();
		String logFileName = logDir.getAbsolutePath() + File.separator + "log-" + new SimpleDateFormat("yyyy-MM-dd").format(logDate) + ".log";
		File logFile = new File(logFileName);
		if (!logFile.exists())
			return null;			
		String line;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(logFile));
			try {
				while (( line = input.readLine()) != null){
					if (line.contains(",")) {
						SyncLogModel logModel = getLogModel(line);
						if (logModel != null)
							logList.add(logModel);
					}
				}
			}
			finally {
				 input.close();
			 }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
		return logList;
	}
	
	/**
	 * Takes a Comma Separated line and creates an object of type {@link SyncLogModel}
	 */
	private static SyncLogModel getLogModel(String line) {
		SyncLogModel syncLogModel = new SyncLogModel();
		// syncId
		if (line.contains(",")) {
			syncLogModel.setSyncId(Integer.parseInt(line.substring(0,line.indexOf(","))));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		// syncDate
		if (line.contains(",")) {
			try {
				DateFormat df =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				syncLogModel.setSyncDate(df.parse(line.substring(0,line.indexOf(","))));
				line=line.substring(line.indexOf(",") + 1);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}else
			return null;
		
		// providerId
		if (line.contains(",")) {
			syncLogModel.setProviderId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		
		// deviceId
		if (line.contains(",")) {
			syncLogModel.setDeviceId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		
		// 	householdId;
		if (line.contains(",")) {
			syncLogModel.setHouseholdId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return syncLogModel;
		
		// fileName;
		if (line.contains(",")) {
			syncLogModel.setFileName(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return syncLogModel;
		
		// fileSize;
		if (line.contains(","))
			syncLogModel.setFileSize(line.substring(0,line.indexOf(",")));
		else
			syncLogModel.setFileSize(line);
		
		return syncLogModel;
	
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.mclinic.MclinicService#getAllSyncLogs()
	 */
	public List<String> getAllSyncLogs() {
		List<String> logFiles = new ArrayList<String>();
		List<Date> logDates = new ArrayList<Date>();
		File logDir=MclinicUtil.getMclinicSyncLogDir();
		DateFormat df =new SimpleDateFormat("yyyy-MM-dd");
		for (File file : logDir.listFiles()) {
			String fileName=file.getName();
			if (fileName.contains("-") && fileName.contains(".")) {
				try {
					fileName=fileName.substring(fileName.indexOf("-")+1,fileName.lastIndexOf("."));
					Date date = df.parse(fileName);
					logDates.add(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		if (!logDates.isEmpty()) {
			df =new SimpleDateFormat("yyyy-MMM-dd");
			DateCompare compare = new DateCompare();
			Collections.sort(logDates, compare);
			for (Date date : logDates)
				logFiles.add(df.format(date));
		}
		return logFiles;
	}
	
	class DateCompare implements Comparator<Date> {
		public int compare(Date one, Date two) {
			return two.compareTo(one);
		}
	}
}
