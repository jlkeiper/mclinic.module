package org.openmrs.module.mclinic.reporting.metadata;

import org.apache.commons.lang.StringUtils;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

public class ExtendedDefinition extends BaseOpenmrsMetadata {

	public static final String DEFINITION_PROPERTY_FORM = "mclinic.property.form";

	private Integer id;

	private CohortDefinition cohortDefinition;

	private Set<DefinitionProperty> properties;

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

	/**
	 * Get the cohort cohortDefinition object
	 *
	 * @return the cohort cohortDefinition object
	 */
	public CohortDefinition getCohortDefinition() {
		return cohortDefinition;
	}

	/**
	 * Set the cohort cohortDefinition object
	 *
	 * @param cohortDefinition the cohort cohortDefinition object
	 */
	public void setCohortDefinition(final CohortDefinition cohortDefinition) {
		this.cohortDefinition = cohortDefinition;
	}

	/**
	 * Get the properties for the cohort cohortDefinition
	 *
	 * @return the properties
	 */
	public Set<DefinitionProperty> getProperties() {
		if (properties == null)
			return new LinkedHashSet<DefinitionProperty>();
		return properties;
	}

	/**
	 * Search for a certain property from all properties for the extended cohortDefinition
	 *
	 * @param property the property
	 * @return the matching cohortDefinition property element
	 */
	public Boolean containsProperty(final String property) {
		for (DefinitionProperty definitionProperty : properties) {
			if (StringUtils.equalsIgnoreCase(definitionProperty.getProperty(), property))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Set the properties for the cohort cohortDefinition
	 *
	 * @param properties the properties
	 */
	public void setProperties(final Set<DefinitionProperty> properties) {
		this.properties = properties;
	}

	/**
	 * Add a cohortDefinition property to the extended cohortDefinition
	 *
	 * @param definitionProperty the cohortDefinition property
	 */
	public void addDefinitionProperty(final DefinitionProperty definitionProperty) {
		definitionProperty.setExtendedDefinition(this);
		getProperties().add(definitionProperty);
	}
}
