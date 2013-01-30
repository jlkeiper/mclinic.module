package org.openmrs.module.mclinic.api.task;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.XformsQueue;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;
import org.openmrs.module.mclinic.api.utils.XformEditor;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

/**
 * Processes xforms Split Queue entries.
 * 
 * Splits composite forms from the drop queue
 * When the processing is successful, the queue is submitted to the xforms queue or sent to a HRS System if its household
 * For unsuccessful processing, the queue is put in the xforms error folder.
 * 
 * @author Samuel Mbugua
 *
 */
@Transactional
public class MclinicSplitProcessor {

	private static final Log log = LogFactory.getLog(MclinicSplitProcessor.class);
	private static Boolean isRunning = false; // allow only one running
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private MclinicService mclinicService;
	
	public MclinicSplitProcessor(){
		try{
			//docBuilder = docBuilderFactory.newDocumentBuilder();
			this.getMclinicService();
		}
		catch(Exception e){
			log.error("Problem occurred while creating mclinic service", e);
		}
	}

	/**
	 * Process all existing queue entries in the xform queue
	 * @param queue 
	 */
	private boolean splitHouseholdForm(XformsQueue queue) throws APIException {
		String formData = queue.getFormData();
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));
			log.debug("Splitting Household xforms");
			XformEditor.createIndividualFiles(doc);
		}
		catch (Throwable t) {
			log.error("Error splitting document", t);
			//Move form to error queue
			saveFormInError(queue.getFileSystemUrl());
			mclinicService.saveErrorInDatabase(MclinicUtil.
					createError(getFormName(queue.getFileSystemUrl()), "Error splitting document", t.getMessage()));
			return false;
		}
		return true;
	}
	
	/**
	 * Split the next pending xform split item. If there are no pending
	 * items in the split_dir, this method simply returns quietly.
	 * 
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public void splitForms() {
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("XForms Household splitting process aborting (another process already running)");
				return;
			}
			isRunning = true;
		}
		try {			
			File pendingSplitDir = MclinicUtil.getMclinicPendingSplitDir();
			for (File file : pendingSplitDir.listFiles()) {
				XformsQueue queue = mclinicService.getMclinicQueue(file.getAbsolutePath());
				
				if (splitHouseholdForm(queue))
					//Move form to archive
					//TODO: Http post to a household system specified by a global property
					saveFormInArchive(queue.getFileSystemUrl());
			}
		}
		catch(Exception e){
			log.error("Problem occured while splitting", e);
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
	 * Archives an xform after successful processing
	 */
	private void saveFormInArchive(String formPath){
		String archiveFilePath= MclinicUtil.getMclinicArchiveDir(new Date()).getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, archiveFilePath);
	}

	
	private void saveFormInError(String formPath){
		String errorFilePath= MclinicUtil.getMclinicErrorDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, errorFilePath);
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
	 * @return MclinicService to be used by the process
	 */
	private MclinicService getMclinicService() {
		if (mclinicService == null) {
			try {
				mclinicService= (MclinicService)Context.getService(MclinicService.class);
			}catch (APIException e) {
				log.debug("MclinicService not found");
				return null;
			}
		}
		return mclinicService;
	}
	
}