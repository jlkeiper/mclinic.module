package org.openmrs.module.mclinic.api.task;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.XformsQueue;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.MclinicConstants;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;
import org.openmrs.module.mclinic.api.utils.SyncLogger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Samuel Mbugua
 *
 */
@Transactional
public class MclinicRawFormsProcessor {

	private static final Log log = LogFactory.getLog(MclinicRawFormsProcessor.class);
	private static Boolean isRunning = false; // allow only one running
	private SyncLogger syncLogger;
	
	/**
	 * Convenience constructor
	 */
	public MclinicRawFormsProcessor(){}

	/**
	 * Send household form to split directory for splitting
	 * @param queue 
	 */
	private void processHouseholdForm(XformsQueue queue) throws APIException {
		log.debug("Transforming xforms drop directory queue");
		//queue form for splitting
		saveFormInPendingSplit(queue.getFileSystemUrl());
	}
	
	/**
	 * Send Patient form to queue for processing
	 * @param queue
	 */
	private void processPatientForm(XformsQueue queue) {
		saveFormInQueue(queue.getFileSystemUrl());
	}
	
	/**
	 * Transform the next drop directory queue entry. If there are no pending
	 * items in the queue, this method simply returns quietly.
	 * 
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public void processRawFormsQueue() {
		MclinicService mhs;
		AdministrationService administrationService = Context.getAdministrationService();
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("RawFormsQueue processor aborting (another processor already running)");
				return;
			}
			isRunning = true;
		}
		try {
			mhs= (MclinicService)Context.getService(MclinicService.class);
		}catch (APIException e) {
			log.debug("MclinicService not found");
			return;
		}
		try {			
			File queueDir = MclinicUtil.getMclinicDropDir();
			for (File file : queueDir.listFiles()) {
				XformsQueue queue = mhs.getMclinicQueue(file.getAbsolutePath());
				
				// Log this sync
				SyncLogger logger=getSyncLogger();
				logger.createSyncLog(file);
				// create a way of following two different paths depending on form types-patients/households
				if(administrationService.getGlobalProperty(MclinicConstants.GP_PROCESS_HOUSEHOLD).equalsIgnoreCase("true"))
					processHouseholdForm(queue);
				else
					processPatientForm(queue);
			}
		}
		catch(Exception e){
			log.error("Problem occured while processing Xforms queue", e);
		}
		finally {
			isRunning = false;
		}
	}
	

	/**
	 * Stores a form in a specified folder after processing.
	 */
	private void saveForm(String oldFormPath, String newFormPath){
		try{
			if(oldFormPath != null){
				File file=new File(oldFormPath);
				
				//move the file to specified new directory
				file.renameTo(new File(newFormPath));
			}
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}

	}
	
	/**
	 * Prepare Patient form for processing 
	 * @param formPath
	 */
	private void saveFormInQueue(String formPath){
		String queueFilePath= MclinicUtil.getMclinicQueueDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, queueFilePath);
	}
	
	/**
	 * Prepare Household form for splitting 
	 */
	private void saveFormInPendingSplit(String formPath){
		String pendingSplitFilePath= MclinicUtil.getMclinicPendingSplitDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, pendingSplitFilePath);
	}

	/**
	 * Extracts form name from an absolute file path
	 * @param formPath
	 * @return
	 */
	private String getFormName(String formPath) {
		return formPath.substring(formPath.lastIndexOf(File.separatorChar)); 
	}

	/**
	 * @return SyncLogger to be used by the process
	 */
	private SyncLogger getSyncLogger() {
		if (syncLogger == null) {
			try {
				syncLogger= new SyncLogger();
			}catch (APIException e) {
				log.debug("SyncLogger not found");
				return null;
			}
		}
		return syncLogger;
	}
}

