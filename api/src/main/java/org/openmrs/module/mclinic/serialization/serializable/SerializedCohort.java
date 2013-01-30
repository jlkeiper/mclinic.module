package org.openmrs.module.mclinic.serialization.serializable;


public class SerializedCohort {

	private Integer id;

	private String name;

	/**
	 * Get the serialized cohort id
	 *
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Set the serialized cohort id
	 *
	 * @param id the id
	 */
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * Get the serialized cohort name
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the serialized cohort name
	 *
	 * @param name the name
	 */
	public void setName(final String name) {
		this.name = name;
	}
}
