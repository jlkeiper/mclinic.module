package org.openmrs.module.mclinic.reporting.service;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mclinic.reporting.metadata.DefinitionProperty;
import org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ReportingConnectorService extends OpenmrsService {

	/**
	 * Service method to save extended information for the reporting cohort definition
	 *
	 * @param extendedDefinition the extended definition
	 * @return saved extended definition
	 * @throws org.openmrs.api.APIException when the saving process failed
	 */
	ExtendedDefinition saveExtendedDefinition(final ExtendedDefinition extendedDefinition) throws APIException;

	/**
	 * Service method to get extended definition by the extended definition uuid
	 *
	 * @param uuid the extended definition uuid
	 * @return the extended definition
	 * @throws org.openmrs.api.APIException when retrieving the extended definition failed
	 */
	@Transactional(readOnly = true)
	ExtendedDefinition getExtendedDefinitionByUuid(final String uuid) throws APIException;

	/**
	 * Service method to get extended definition by the definition
	 *
	 * @param definition the definition
	 * @return the extended definition
	 * @throws org.openmrs.api.APIException when retrieving the extended definition failed
	 */
	@Transactional(readOnly = true)
	ExtendedDefinition getExtendedDefinitionByDefinition(final CohortDefinition definition) throws APIException;

	/**
	 * Service method to get extended definition by the extended definition id
	 *
	 * @param id the extended definition id
	 * @return the extended definition
	 * @throws org.openmrs.api.APIException when retrieving the extended definition failed
	 */
	@Transactional(readOnly = true)
	ExtendedDefinition getExtendedDefinition(final Integer id) throws APIException;

	/**
	 * Service method to get all saved extended definitions
	 *
	 * @return alll saved extended definitions
	 * @throws org.openmrs.api.APIException when retrieving all extended definitions failed
	 */
	@Transactional(readOnly = true)
	List<ExtendedDefinition> getAllExtendedDefinition() throws APIException;

	/**
	 * Service method to save or update a definition property
	 *
	 * @param definitionProperty the definition property
	 * @return the saved definition property
	 * @throws org.openmrs.api.APIException when saving the definition property failed
	 */
	DefinitionProperty saveDefinitionProperty(final DefinitionProperty definitionProperty) throws APIException;

	/**
	 * Service method to get the definition property by the definition property id
	 *
	 * @param id the definition property id
	 * @return the definition property or null
	 * @throws org.openmrs.api.APIException when getting the definition property failed
	 */
	DefinitionProperty getDefinitionProperty(final Integer id) throws APIException;

}
