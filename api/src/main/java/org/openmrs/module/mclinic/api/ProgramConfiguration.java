package org.openmrs.module.mclinic.api;

import org.openmrs.BaseOpenmrsMetadata;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProgramConfiguration extends BaseOpenmrsMetadata {

	private Integer id;

	private Set<ConfiguredConcept> configuredConcepts;

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
	 * Return set of configured concepts
	 *
	 * @return
	 */
	public Set<ConfiguredConcept> getConfiguredConcepts() {
		return configuredConcepts;
	}

	/**
	 * Set the configured concepts
	 *
	 * @param configuredConcepts
	 */
	public void setConfiguredConcepts(final Set<ConfiguredConcept> configuredConcepts) {
		this.configuredConcepts = configuredConcepts;
	}

	/**
	 * Add a configured concept into the configuration
	 *
	 * @param configuredConcept
	 */
	public void addConfiguredConcept(final ConfiguredConcept configuredConcept) {
		if (configuredConcepts == null)
			configuredConcepts = new LinkedHashSet<ConfiguredConcept>();
		configuredConcepts.add(configuredConcept);
	}
}
