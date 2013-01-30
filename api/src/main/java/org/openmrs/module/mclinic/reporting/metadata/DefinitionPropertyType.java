package org.openmrs.module.mclinic.reporting.metadata;

import org.openmrs.BaseOpenmrsMetadata;

public class DefinitionPropertyType extends BaseOpenmrsMetadata {

	private Integer id;

	/**
	 * @return id - The unique Identifier for the object
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * @param id - The unique Identifier for the object
	 */
	@Override
	public void setId(final Integer id) {
		this.id = id;
	}
}
