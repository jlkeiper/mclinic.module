package org.openmrs.module.mclinic.api.task;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.XformsQueue;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.MclinicConstants;
import org.openmrs.module.mclinic.api.utils.MclinicFileUploader;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;
import org.openmrs.module.mclinic.api.utils.XformEditor;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.Date;


/**
 * Processes Patient Xforms in the Queue directory
 * Submits all the patient forms to the xforms module for processing
 * 
 * @author Samuel Mbugua
 *
 */
@Transactional
public class MclinicQueueProcessor {

	private static final Log log = LogFactory.getLog(MclinicQueueProcessor.class);
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private XPathFactory xPathFactory;
	private MclinicService mhs;
	// allow only one running instance
	private static Boolean isRunning = false;

	public MclinicQueueProcessor(){
		try{
			docBuilder = docBuilderFactory.newDocumentBuilder();
			this.getMclinicService();
		}
		catch(Exception e){
			log.error("Problem occurred while creating document builder", e);
		}
	}
	/**
	 * Process an existing entry in the queue directory
	 * @param filePath file path of the queue
     * @param queue the form queue to process
	 */
	private void processQueueForms(String filePath, XformsQueue queue) throws APIException {
		log.debug("Processing patient forms and sending them to the xform module");
		try {
			String formData = queue.getFormData();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			XPathFactory xpf = getXPathFactory();
			XPath xp = xpf.newXPath();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));
			Node curNode=(Node)  xp.evaluate(MclinicConstants.PATIENT_NODE, doc, XPathConstants.NODE);
			String patientId = xp.evaluate(MclinicConstants.PATIENT_ID, curNode);
			String patientIdentifier = xp.evaluate(MclinicConstants.PATIENT_IDENTIFIER, curNode); 
			String birthDate = xp.evaluate(MclinicConstants.PATIENT_BIRTHDATE, curNode);
			String familyName = xp.evaluate(MclinicConstants.PATIENT_FAMILYNAME, curNode);
			String givenName = xp.evaluate(MclinicConstants.PATIENT_GIVENNAME, curNode);
			String middleName = xp.evaluate(MclinicConstants.PATIENT_MIDDLENAME, curNode);
			
			if (patientId != null) patientId=patientId.trim(); // remove white spaces in patient_id
			
			//If a form has a non-zero patient id its an existing patient forget everything, 
			//otherwise do demographics check
			if (patientId == null || patientId.equalsIgnoreCase("0")) {
				//Ensure there is a patient identifier in the form and 
				// if without names just delete the form
				String tmpIdentifier = MclinicUtil.getPatientIdentifier(doc);
				if (tmpIdentifier == null || tmpIdentifier.trim().equals("")) {
					if ((familyName == null || familyName.trim().equals("")) &&
							(givenName == null || givenName.equals(""))) {
						MclinicUtil.deleteFile(filePath);
						log.info("Deleted an empty individual file");
					} else {
						// form has no patient identifier but has names : move to error
						saveFormInError(filePath);
						mhs.saveErrorInDatabase(MclinicUtil.
								createError(getFormName(filePath), "Error processing patient", 
										"Patient has no identifier, or the identifier provided is invalid"));
					}
					return;
				}
				
				//Ensure Family name and Given names are not blanks
				if (familyName == null || familyName.trim().equals("") || givenName == null || givenName.equals("")) {
					saveFormInError(filePath);
					mhs.saveErrorInDatabase(MclinicUtil.
							createError(getFormName(filePath), "Error processing patient", 
									"Patient has no valid names specified, Family Name and Given Name are required"));
					return;
				}
				
				// ensure patient has birth date
				if (birthDate == null || birthDate.trim().length() == 0 ) {
					//patient has no valid birth-date
					saveFormInError(filePath);
					mhs.saveErrorInDatabase(MclinicUtil.
							createError(getFormName(filePath), "Error processing patient", "Patient has no valid Birthdate"));
					return;
				}
			}
			
			// Ensure there is a valid provider id or name and return provider_id in the form
			curNode=(Node)  xp.evaluate(MclinicConstants.ENCOUNTER_NODE, doc, XPathConstants.NODE);
			Integer providerId=MclinicUtil.getProviderId(xp.evaluate(MclinicConstants.ENCOUNTER_PROVIDER, curNode));
			if ((providerId) == null) {
				// form has no valid provider : move to error
				saveFormInError(filePath);
				mhs.saveErrorInDatabase(MclinicUtil.createError(getFormName(filePath), "Error processing patient form", 
								"Provider for this encounter is not provided, or the provider identifier provided is invalid"));
				return;
			}else
				XformEditor.editNode(filePath, 
						MclinicConstants.ENCOUNTER_NODE + "/" + MclinicConstants.ENCOUNTER_PROVIDER, providerId.toString());
			
			//Ensure if not new it is same person
			if (!MclinicUtil.isNewPatient(patientIdentifier)){
				Patient pat = MclinicUtil.getPatient(patientIdentifier);
				PersonName personName = new PersonName(givenName, middleName, familyName);
				if (!pat.getPersonName().equalsContent(personName)) 
					//saveFormInError(filePath); Mbugua: Commented this as Andy wants to process even when there is an error
					mhs.saveErrorInDatabase(MclinicUtil.
							createError(getFormName(filePath), "Warning: processing patient", 
									"A different person (By Name) exists with the same identifier " +
									"(" + patientIdentifier + "). Encounter added anyway."));
			}
			
			//Finally send to xforms for processing
			MclinicFileUploader.submitXFormFile(filePath);
			saveFormInArchive(filePath);
		}
		catch (Throwable t) {
			log.error("Error while sending form to xform module", t);
			//put file in error queue
			saveFormInError(filePath);
			mhs.saveErrorInDatabase(MclinicUtil.
					createError(getFormName(filePath), "Error sending form to xform module", t.getMessage()));
		}
	}

	/**
	 * Processes each queue entry. If there are no pending
	 * items in the queue, this method simply returns quietly.
	 */
	public void processMclinicQueue() {
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("mclinicQueue processor aborting (another processor already running)");
				return;
			}
			isRunning = true;
		}

		try {			
			File queueDir = MclinicUtil.getMclinicQueueDir();
			for (File file : queueDir.listFiles()) {
				XformsQueue queue = mhs.getMclinicQueue(file.getAbsolutePath());
				processQueueForms(file.getAbsolutePath(), queue);
			}
		}
		catch(Exception e){
			log.error("Problem occured while processing queue", e);
		}
		finally {
			isRunning = false;
		}
	}

	/**
	 * Stores an erred form in the error directory
	 * @param formPath 
	 */
	private void saveFormInError(String formPath){
		String errorFilePath= MclinicUtil.getMclinicErrorDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, errorFilePath);
	}
	
	/**
	 * Stores a new patient file to pending link directory
	 * @param formPath 
	 */
	private void saveFormInArchive(String formPath){
		String archiveFilePath= MclinicUtil.getMclinicArchiveDir(new Date()).getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, archiveFilePath);
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
	 * Extracts form name from an absolute file path
	 * @param formPath
	 * @return
	 */
	private String getFormName(String formPath) {
		return formPath.substring(formPath.lastIndexOf(File.separatorChar)); 
	}
	
	/**
	 * @return XPathFactory to be used for obtaining data from the parsed XML
	 */
	private XPathFactory getXPathFactory() {
		if (xPathFactory == null)
			xPathFactory = XPathFactory.newInstance();
		return xPathFactory;
	}
	
	/**
	 * @return MclinicService to be used by the process
	 */
	private MclinicService getMclinicService() {
		if (mhs == null) {
			try {
				mhs= (MclinicService)Context.getService(MclinicService.class);
			}catch (APIException e) {
				log.debug("MclinicService not found");
				return null;
			}
		}
		return mhs;
	}
	
}