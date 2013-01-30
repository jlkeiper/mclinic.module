package org.openmrs.module.mclinic.web.controller.form;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.mclinic.api.MclinicErrorModel;
import org.openmrs.module.mclinic.api.XformsError;
import org.openmrs.module.mclinic.api.XformsQueue;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.LogArchiver;
import org.openmrs.module.mclinic.api.utils.MclinicConstants;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;
import org.openmrs.module.mclinic.api.utils.XformEditor;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;

/**
 * Controller for xforms errors resolution jsp pages
 * 
 * @author Samuel Mbugua
 */
@Controller
public class ErrorResolverController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private MclinicService mfs;
	
	/**
	 * Controller for Error list jsp page
	 */
	@ModelAttribute("xformsErrors")
	@RequestMapping(value="/module/mclinic/resolveErrors", method=RequestMethod.GET)
	public List<XformsError> populateForm() {
		if (Context.isAuthenticated()) {
			getMclinicService();
			List<XformsError> lstErrors = mfs.getAllXformsErrors();
			List<XformsError> tmpLstErrors = new ArrayList<XformsError>();
			for (XformsError xformsError : lstErrors) {
				if (createFormData(xformsError.getFormName())!=null)
					tmpLstErrors.add(xformsError);
			}
			return tmpLstErrors;
		}
		return null;
	}
	
	@ModelAttribute("warnings")
	@RequestMapping(value="/module/mclinic/processingWarning", method=RequestMethod.GET)
	public List<XformsError> populateWarnings() {
		if (Context.isAuthenticated()) {
			getMclinicService();
			List<XformsError> lstWarnings = mfs.getAllXformsErrors();
			List<XformsError> tmpLstWarnings = new ArrayList<XformsError>();
			for (XformsError xformsError : lstWarnings) {
				if (createFormData(xformsError.getFormName())==null)
					tmpLstWarnings.add(xformsError);
			}
			return tmpLstWarnings;
		}
		return null;
	}
	
	@RequestMapping(value="/module/mclinic/processingWarning", method=RequestMethod.POST)
	public String archiveErrorLogs(HttpSession httpSession) {
		getMclinicService();
		MessageSourceService msa = Context.getMessageSourceService();
		//get all erred logs
		StringBuffer outPutLine = new StringBuffer();
		List<XformsError> lstWarnings = mfs.getAllXformsErrors();
		List<XformsError> tmpLstWarnings = new ArrayList<XformsError>();
		for (XformsError xformsError : lstWarnings) {
			if (createFormData(xformsError.getFormName())==null)
				tmpLstWarnings.add(xformsError);
		}
		for (XformsError XformsError : tmpLstWarnings) {
			outPutLine.append(getOutputLine(XformsError));
		}
		
		if (outPutLine.length()> 0) {
			if (LogArchiver.createArchiveLog(outPutLine.toString(), "Problem_log")){
				for (XformsError xformsError : tmpLstWarnings) {
					mfs.deleteError(xformsError);
				}
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, 
						msa.getMessage("mclinic.processingWarning.clear.success"));
			}else
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, 
						msa.getMessage("mclinic.processingWarning.clear.error"));
		}
		return "/module/mclinic/processingWarning";
	}
	
	/**
	 * Controller for commentOnError jsp Page
	 */
	@ModelAttribute("errorFormComment")
	@RequestMapping(value="/module/mclinic/resolveErrorComment", method=RequestMethod.GET)
	public MclinicErrorModel populateCommentForm(@RequestParam Integer errorId) {
		return getErrorObject(errorId);
	}
	
	/**
	 * Controller for commentOnError post jsp Page
	 */
	@RequestMapping(value="/module/mclinic/resolveErrorComment", method=RequestMethod.POST)
	public String saveComment(HttpSession httpSession, @RequestParam Integer errorId, @RequestParam String comment) {
		if (comment.trim().length() > 0) {
			getMclinicService();
			XformsError error=mfs.getErrorById(errorId);
			error.setComment(comment);
			error.setCommentedBy(Context.getAuthenticatedUser());
			error.setDateCommented(new Date());
			mfs.saveErrorInDatabase(error);
		}else
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid Comment" );
		return "redirect:resolveErrors.list";		
	}
	
	/**
	 * Controller for formDisplay pop-up jsp Page
	 */
	@RequestMapping(value="/module/mclinic/formDisplay", method=RequestMethod.GET)
	public String populateDisplayForm(ModelMap model, HttpServletRequest request) {
		String strErrorId = request.getParameter("errorId");
		try {
			getMclinicService();
			Integer errorId = Integer.parseInt(strErrorId);
			XformsError error=mfs.getErrorById(errorId);
			if (error != null)
				model.addAttribute("errorObject", getErrorObject(errorId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/module/mclinic/formDisplay";
	}

	/**
	 * Controller for resolveError jsp Page
	 */
	@ModelAttribute("errorFormResolve")
	@RequestMapping(value="/module/mclinic/resolveError", method=RequestMethod.GET)
	public MclinicErrorModel populateErrorForm(@RequestParam Integer errorId) {
		return getErrorObject(errorId);	
	}


	/**
	 * Controller for resolveError post jsp Page
	 */
	@RequestMapping(value="/module/mclinic/resolveError", method=RequestMethod.POST)
	public String resolveError(HttpSession httpSession, @RequestParam String givenName,
								@RequestParam Integer errorId, @RequestParam String errorItemAction,
								@RequestParam String birthDate, @RequestParam String patientIdentifier,
								@RequestParam String providerId, @RequestParam String familyName,
								@RequestParam String middleName, @RequestParam String gender){
		String filePath;
		
		// user must be authenticated (avoids authentication errors)
		if (Context.isAuthenticated()) {
			if (!Context.getAuthenticatedUser().hasPrivilege(
					MclinicConstants.PRIV_RESOLVE_XFORM_ERROR)) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "mclinic.action.noRights");
				return "redirect:resolveErrors.list";
			}
				
			getMclinicService();
			
			// fetch the XformsError item from the database
			XformsError errorItem = mfs.getErrorById(errorId);
			filePath= MclinicUtil.getMclinicErrorDir().getAbsolutePath() + errorItem.getFormName();
			
			// assign birth-date
			if ("assignBirthdate".equals(errorItemAction)) {
				if (birthDate!=null && birthDate.trim()!="") {
					if (XformEditor.editNode(filePath, 
							MclinicConstants.PATIENT_NODE + "/" + MclinicConstants.PATIENT_BIRTHDATE, birthDate)) {
						// put form in queue for normal processing
						saveForm(filePath, MclinicUtil.getMclinicQueueDir().getAbsolutePath() + errorItem.getFormName());
						// delete the xform error queue item
						mfs.deleteError(errorItem);
					} 
				}else {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Birthdate was not assigned, Null object entered");
					return "redirect:resolveErrors.list";
				}
			}
			
			// assign gender
			if ("assignGender".equals(errorItemAction)) {
				if (gender!=null && gender.trim()!="") {
					if (XformEditor.editNode(filePath, 
							MclinicConstants.PATIENT_NODE + "/" + MclinicConstants.PATIENT_SEX, gender)) {
						// put form in queue for normal processing
						saveForm(filePath, MclinicUtil.getMclinicQueueDir().getAbsolutePath() + errorItem.getFormName());
						// delete the xform error queue item
						mfs.deleteError(errorItem);
					} else {
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Gender was not assigned, Node does not exist in this form");
						return "redirect:resolveErrors.list";
					}
				}else {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Gender was not assigned, You did not select gender to assign");
					return "redirect:resolveErrors.list";
				}
			}
			
			// assign person names
			if ("assignNames".equals(errorItemAction)) {
				if (givenName != null && givenName.trim() != "" 
					&& familyName != null && familyName.trim() != "") {
					boolean editted = false;
					editted = XformEditor.editNode(filePath, 
							MclinicConstants.PATIENT_NODE + "/" + MclinicConstants.PATIENT_GIVENNAME, givenName);
					editted = XformEditor.editNode(filePath, 
							MclinicConstants.PATIENT_NODE + "/" + MclinicConstants.PATIENT_FAMILYNAME, familyName);
					editted = XformEditor.editNode(filePath, 
							MclinicConstants.PATIENT_NODE + "/" + MclinicConstants.PATIENT_MIDDLENAME, middleName);
					if (editted){
						// put form in queue for normal processing
						saveForm(filePath, MclinicUtil.getMclinicQueueDir().getAbsolutePath() + errorItem.getFormName());
						// delete the xform error queue item
						mfs.deleteError(errorItem);
					}else {
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Error editting person names");
						return null;
					}
				} else {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Family and Given Names are required!");
					return null;
				}
			}
			
			// assign a new identifier
			if ("newIdentifier".equals(errorItemAction)) {
				if (patientIdentifier != null && patientIdentifier.trim() != "") {
					if (reverseNodes(filePath, patientIdentifier)) {
						
						// put form in queue for normal processing
						saveForm(filePath, MclinicUtil.getMclinicQueueDir().getAbsolutePath() + errorItem.getFormName());
						
						// delete the xform error queue item
						mfs.deleteError(errorItem);
					}
				}
				else {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "mclinic.resolveErrors.action.newIdentifier.error");
					return "redirect:resolveErrors.list";
				}
			}
			
			// link a provider
			if ("linkProvider".equals(errorItemAction)) {
				if (providerId != null && providerId.trim() != "") {
					providerId = Context.getUserService().getUser(Integer.parseInt(providerId)).getSystemId();
					if (XformEditor.editNode(filePath, 
							MclinicConstants.ENCOUNTER_NODE + "/" + MclinicConstants.ENCOUNTER_PROVIDER, providerId)) {
						// put form in queue for normal processing
						saveForm(filePath, MclinicUtil.getMclinicQueueDir().getAbsolutePath() + errorItem.getFormName());
						// delete the xform error queue item
						mfs.deleteError(errorItem);
					}
				}
				else {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "(Null) Invalid provider ID");
					return "redirect:resolveErrors.list";
				}
			}
			
			if ("editXform".equals(errorItemAction)) {
				return "redirect:editXform.form?errorId="+errorId;
			}
			else if ("createPatient".equals(errorItemAction)) {
				// put form in queue for normal processing
				saveForm(filePath, MclinicUtil.getMclinicQueueDir().getAbsolutePath() + errorItem.getFormName());
				
				// delete the xform error queue item
				mfs.deleteError(errorItem);				
			}
			else if ("deleteError".equals(errorItemAction)) {
				// delete the xform error queue item
				mfs.deleteError(errorItem);
				
				//and delete from the file system
				MclinicUtil.deleteFile(filePath);
				
			}
			else if ("deleteComment".equals(errorItemAction)) {
				//set comment to null and save
				errorItem.setComment(null);
				mfs.saveErrorInDatabase(errorItem);
			}
			
			else if ("noChange".equals(errorItemAction)) {
				// do nothing here
			}
		}
		
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "mclinic.resolveErrors.action.success"); 
		return "redirect:resolveErrors.list";		
	}
	
	/**
	 * Controller for editXform jsp Page
	 *
	@ModelAttribute("xformEditModels")
	@RequestMapping(value="/module/mclinic/editXform", method=RequestMethod.GET)
	public List<XformEditModel> showEditXform(HttpServletRequest request) {
		String strErrorId = request.getParameter("errorId");
		try {
			getMclinicService();
			Integer errorId = Integer.parseInt(strErrorId);
			XformsError error=mfs.getErrorById(errorId);
			if (error != null) {
				String filePath= MclinicUtil.getMclinicErrorDir().getAbsolutePath() + error.getFormName();
				return XFormEditor.createMapFromFile(filePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Controller for editXform jsp Page
	 */
	@ModelAttribute("xformEditModels")
	@RequestMapping(value="/module/mclinic/editXform", method=RequestMethod.GET)
	public String showEditXform(ModelMap model, HttpServletRequest request) {
		String strErrorId = request.getParameter("errorId");
		try {
			getMclinicService();
			Integer errorId = Integer.parseInt(strErrorId);
			XformsError error=mfs.getErrorById(errorId);
			if (error != null)
				model.addAttribute("errorObject", getErrorObject(errorId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/module/mclinic/editXform";
	}
	
	/**
	 * Controller for editXform jsp Page
	 */
	@RequestMapping(value="/module/mclinic/editXform", method=RequestMethod.POST)
	public String saveEditedXform(HttpSession httpSession, @RequestParam String xmlString,
			 @RequestParam Integer errorId, @RequestParam String action) {
		if (action != null) {
			MessageSourceService mss = Context.getMessageSourceService();
			String save = mss.getMessage("mclinic.resolveErrors.editXform.save");
			if (action.equals(save)) {
				try {
					getMclinicService();
					XformsError error=mfs.getErrorById(errorId);
					if (error != null){
						String filePath = MclinicUtil.getMclinicErrorDir().getAbsolutePath() + error.getFormName();
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder docBuilder = factory.newDocumentBuilder();
						Document doc = docBuilder.parse(IOUtils.toInputStream(xmlString));
						XformEditor.saveXMLDocument(doc, filePath);
						httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "mclinic.resolveErrors.editXform.success");
					}
				} catch (Exception e) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "mclinic.resolveErrors.editXform.error");
					e.printStackTrace();
				}
			}
		}
		return "redirect:resolveError.form?errorId=" + errorId;
	}
	
	private String formatXML(String xmlString) {
		try {
			Source xmlInput = new StreamSource(new StringReader(xmlString));
			StreamResult xmlOutput = new StreamResult(new StringWriter());
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}  
		return xmlString;
	}
	
	/**
	 * Given an id, this method  creates an error model
	 * @param errorId
	 * @return List of errors
	 */
	private MclinicErrorModel  getErrorObject(Integer errorId) {
		getMclinicService();
		XformsError error= mfs.getErrorById(errorId);
		if (error !=null) {
			String formName = error.getFormName();
			String filePath = getAbsoluteFilePath(formName);
			error.setFormName(formatXML((createFormData(error.getFormName()))));
			MclinicErrorModel errorForm = new MclinicErrorModel(error);
			errorForm.setFormPath(filePath);
			return errorForm;
		}
		return null;
	}
	
	/**
	 * Converts an xml file specified by <b>formPath</b> to a string
	 * @param formPath
	 * @return String representation of the file
	 */
	private String createFormData (String formName) {
		getMclinicService();
		XformsQueue queue= mfs.getMclinicQueue(MclinicUtil.getMclinicErrorDir().getAbsolutePath()
								+ formName);
		return queue.getFormData();
	}
	
	/**
	 * Takes in an Xform Queue and returns an absolute Path
	 * @param formPath
	 * @return String absolute path of the file
	 */
	private String getAbsoluteFilePath (String formName) {
		getMclinicService();
		XformsQueue queue= mfs.getMclinicQueue(MclinicUtil.getMclinicErrorDir().getAbsolutePath()
								+ formName);
		return queue.getFileSystemUrl();
	}

	/**
	 * Stores a form in a specified folder
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
	 * Reverses patient Identifier nodes for a form with more than one
	 * @param filePath
	 * @param patientIdentifier
	 * @return
	 */
	private boolean reverseNodes(String filePath, String patientIdentifier) {
		try {
			XformEditor.editNode(filePath, MclinicConstants.PATIENT_NODE + "/" + MclinicConstants.PATIENT_IDENTIFIER, patientIdentifier);
		}
		catch (Throwable t) {
			log.error("Error reversing nodes", t);
			return false;
		}
		return true;
	}
	
	private String getOutputLine(XformsError xformsError) {
		return xformsError.getId() + ", " + xformsError.getError() + ", " + xformsError.getErrorDetails() 
		+ ", " + xformsError.getFormName() + ", " + xformsError.getDateCreated() + "\n";
	}
	
	private void getMclinicService(){
		if (mfs == null)
			mfs=(MclinicService)Context.getService(MclinicService.class);
	}
}	