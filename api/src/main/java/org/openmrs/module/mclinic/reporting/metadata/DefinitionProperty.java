package org.openmrs.module.mclinic.reporting.metadata;

import org.openmrs.BaseOpenmrsMetadata;

public class DefinitionProperty extends BaseOpenmrsMetadata {

	private Integer id;

	private String property;

	private String propertyValue;

	private String propertyDescription;

	private ExtendedDefinition extendedDefinition;

	/**
	 * Default constructor to create definition property
	 */
	public DefinitionProperty() {
	}

	/**
	 * Constructor to create definition property
	 *
	 * @param property            the property
	 * @param propertyValue       the property value
	 * @param propertyDescription the property description
	 */
	public DefinitionProperty(final String property, final String propertyValue, final String propertyDescription) {
		this.property = property;
		this.propertyValue = propertyValue;
		this.propertyDescription = propertyDescription;
	}

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
	 * Get the property of the definition property
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Set the property of the definition property
	 *
	 * @param property the property
	 */
	public void setProperty(final String property) {
		this.property = property;
	}

	/**
	 * Get the property value of the definition property
	 *
	 * @return the property value
	 */
	public String getPropertyValue() {
		return propertyValue;
	}

	/**
	 * Set the property value of the definition property
	 *
	 * @param propertyValue the property value
	 */
	public void setPropertyValue(final String propertyValue) {
		this.propertyValue = propertyValue;
	}

	/**
	 * Set the property description of the definition property
	 *
	 * @return the property description
	 */
	public String getPropertyDescription() {
		return propertyDescription;
	}

	/**
	 * Set the property description of the definition property
	 *
	 * @param propertyDescription the property description
	 */
	public void setPropertyDescription(final String propertyDescription) {
		this.propertyDescription = propertyDescription;
	}

	/**
	 * The inverse reference to the definition property
	 *
	 * @return the definition property
	 */
	public ExtendedDefinition getExtendedDefinition() {
		return extendedDefinition;
	}

	/**
	 * Set the inverse reference to the definition property
	 *
	 * @param extendedDefinition the definition property
	 */
	public void setExtendedDefinition(final ExtendedDefinition extendedDefinition) {
		this.extendedDefinition = extendedDefinition;
	}
}
