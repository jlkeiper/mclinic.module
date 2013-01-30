package org.openmrs.module.mclinic.api;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

import java.util.Date;

/**
 * Xform object, stores downloadable forms
 * @author  Samuel Mbugua
 *
 */
public class MclinicXform extends BaseOpenmrsObject implements Auditable{
	
	private Integer mclinicXformId;
	private Integer xformId;
	private String xformName;
	private String xformMeta;
	private String xformXml;
	private ProgramConfiguration program;
	private User creator;
	private Date dateCreated;

	/**
	 * @return the mclinicXformId
	 */
	public Integer getMclinicXformId() {
		return mclinicXformId;
	}

	/**
	 * @param mclinicXformId the mclinicXformId to set
	 */
	public void setMclinicXformId(Integer mclinicXformId) {
		this.mclinicXformId = mclinicXformId;
	}

	/**
	 * @return the xformId
	 */
	public Integer getXformId() {
		return xformId;
	}

	/**
	 * @param xformId the xformId to set
	 */
	public void setXformId(Integer xformId) {
		this.xformId = xformId;
	}

	/**
	 * @return the xformName
	 */
	public String getXformName() {
		return xformName;
	}

	/**
	 * @param xformName the xformName to set
	 */
	public void setXformName(String xformName) {
		this.xformName = xformName;
	}

	/**
	 * @return the xformMeta
	 */
	public String getXformMeta() {
		return xformMeta;
	}

	/**
	 * @param xformMeta the xformMeta to set
	 */
	public void setXformMeta(String xformMeta) {
		this.xformMeta = xformMeta;
	}

	/**
	 * @return the xformXml
	 */
	public String getXformXml() {
		return xformXml;
	}

	/**
	 * @param xformXml the xformXml to set
	 */
	public void setXformXml(String xformXml) {
		this.xformXml = xformXml;
	}

	/**
	 * @return the program
	 */
	public ProgramConfiguration getProgram() {
		return program;
	}

	/**
	 * @param program the program to set
	 */
	public void setProgram(ProgramConfiguration program) {
		this.program = program;
	}

	/**
	 * @return Returns the creator.
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            The creator to set.
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}

	/**
	 * @return Returns the dateCreated.
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            The dateCreated to set.
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public User getChangedBy() {
		return null;
	}

	public Date getDateChanged() {
		return null;
	}

	public void setChangedBy(User changedBy) {}

	public void setDateChanged(Date dateChanged) {}

	public Integer getId() {
		return this.getMclinicXformId();
	}

	public void setId(Integer id) {
		this.setMclinicXformId(id);
	}

}
