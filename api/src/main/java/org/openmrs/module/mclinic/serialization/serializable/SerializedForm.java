package org.openmrs.module.mclinic.serialization.serializable;


public class SerializedForm {

	private Integer patientId;

	private Integer formId;

	/**
	 * Create new patient form information
	 *
	 * @param patientId the patient id
	 * @param formId    the form id
	 */
	public SerializedForm(final Integer patientId, final Integer formId) {
		this.patientId = patientId;
		this.formId = formId;
	}

	/**
	 * Get the patient id for which the form is applicable to
	 *
	 * @return the patient id
	 */
	public Integer getPatientId() {
		return patientId;
	}

	/**
	 * Set the patient id for which the form is applicable to
	 *
	 * @param patientId the patient id
	 */
	public void setPatientId(final Integer patientId) {
		this.patientId = patientId;
	}

	/**
	 * Get the form id for which the patient id is applicable to
	 *
	 * @return the form id
	 */
	public Integer getFormId() {
		return formId;
	}

	/**
	 * Set the form id which the patient id is applicable to
	 *
	 * @param formId the form id
	 */
	public void setFormId(final Integer formId) {
		this.formId = formId;
	}
}
