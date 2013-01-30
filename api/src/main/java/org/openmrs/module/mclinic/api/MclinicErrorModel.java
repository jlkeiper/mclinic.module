package org.openmrs.module.mclinic.api;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 * This class is only used as the model for the ResolveErrosFormController and
 * its jsp page
 * 
 * @author  Samuel Mbugua
 */
public class MclinicErrorModel extends XformsError {
	
	protected final Log log = LogFactory.getLog(getClass());
	private DocumentBuilderFactory documentBuilderFactory;
	
	// data from the formData
	private String name = "";
	private String birthdate = "";
	private String gender = "";
	private String identifier = "";
	
	// data from the formData encounter section
	private String location = "";
	private String encounterDate = "";
	private String formModelName = "";
	private String formId = "";
	private String formPath="";
	
	/**
	 * Creates a model object from the given XformsError. Parses data out of
	 * the &lt;patient> section to fill in the name/birthdate/etc fields
	 * 
	 * @param error XformsError to duplicate
	 */
	public MclinicErrorModel(XformsError error) {
		
		setXformsErrorId(error.getXformsErrorId());
		setFormName(error.getFormName());
		setError(error.getError());
		setErrorDetails(error.getErrorDetails());
		setDateCreated(error.getDateCreated());
		
		//For resolve form
		setComment(error.getComment());
		setCommentedBy(error.getCommentedBy());
		setDateCommented(error.getDateCommented());
		
		if (getFormName() != null && getFormName().length() > 0) {
			try {
				Document formDataDoc = getDocumentForErrorQueueItem(getFormName());
				XPath xp = getXPathFactory().newXPath();
				
				setName(xp.evaluate("/form/patient/patient.given_name", formDataDoc) + " " +
						xp.evaluate("/form/patient/patient.middle_name", formDataDoc) + " " +
						xp.evaluate("/form/patient/patient.family_name", formDataDoc));
				
				setBirthdate(xp.evaluate("/form/patient/patient.birthdate", formDataDoc));
				setIdentifier(xp.evaluate("/form/patient/patient.medical_record_number", formDataDoc));
				setGender(xp.evaluate("/form/patient/patient.sex", formDataDoc));
				
				// parse the encounter info from the form data
				String location = xp.evaluate("/form/encounter/encounter.location_id", formDataDoc);
				setLocation(location.substring(location.indexOf("^") + 1));
				setEncounterDate(xp.evaluate("/form/encounter/encounter.encounter_datetime", formDataDoc));
				setFormModelName(xp.evaluate("/form/@name", formDataDoc));
				setFormId(xp.evaluate("/form/@version", formDataDoc));
				
			}
			catch (Exception e) {
				log.warn("Unable to get xml document from formData for xformserror with id: " + error.getXformsErrorId(), e);
			}
		}
	}

	/**
     * @return the birthdate
     */
    public String getBirthdate() {
    	return birthdate;
    }

	/**
     * @param birthdate the birthdate to set
     */
    public void setBirthdate(String birthdate) {
    	this.birthdate = birthdate;
    }

	/**
     * @return the gender
     */
    public String getGender() {
    	return gender;
    }

	/**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
    	this.gender = gender;
    }

	/**
     * @return the identifier
     */
    public String getIdentifier() {
    	return identifier;
    }

	/**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
    	this.identifier = identifier;
    }

	/**
     * @return the name
     */
    public String getName() {
    	return name;
    }

	/**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }

	/**
     * @return the encounterDate
     */
    public String getEncounterDate() {
    	return encounterDate;
    }

	/**
     * @param encounterDate the encounterDate to set
     */
    public void setEncounterDate(String encounterDate) {
    	this.encounterDate = encounterDate;
    }

	/**
     * @return the location
     */
    public String getLocation() {
    	return location;
    }

	/**
     * @param location the location to set
     */
    public void setLocation(String location) {
    	this.location = location;
    }
    
	/**
     * @return the formModelName
     */
    public String getFormModelName() {
    	return formModelName;
    }

	/**
     * @param formModelName the formModelName to set
     */
    public void setFormModelName(String formModelName) {
    	this.formModelName = formModelName;
    }
    
	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	public String getFormPath() {
		return formPath;
	}

	public void setFormPath(String formPath) {
		this.formPath = formPath;
	}
	
	/**
	 * Fetch the xml doc for this error
	 * 
	 * @param formData
	 * @throws Exception
	 */
	public Document getDocumentForErrorQueueItem(String formData) throws Exception {
		DocumentBuilderFactory dbf = getDocumentBuilderFactory();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(IOUtils.toInputStream(formData));
		return doc;
	}
	
	private DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null)
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
		return documentBuilderFactory;
	}
	
	private XPathFactory xPathFactory;
	
	/**
	 * @return XPathFactory to be used for obtaining data from the parsed XML
	 */
	private XPathFactory getXPathFactory() {
		if (xPathFactory == null)
			xPathFactory = XPathFactory.newInstance();
		return xPathFactory;
	}
	
}