package org.openmrs.module.mclinic.reporting.db;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.mclinic.reporting.metadata.DefinitionProperty;
import org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;

import java.util.List;

public interface ReportingConnectorDAO {

	/**
	 * DAO method to save extended information for the reporting cohort definition
	 *
	 * @param extendedDefinition the extended definition
	 * @return saved extended definition
	 * @throws org.openmrs.api.db.DAOException
	 *          when the saving process failed
	 */
	ExtendedDefinition saveExtendedDefinition(final ExtendedDefinition extendedDefinition) throws DAOException;

	/**
	 * DAO method to get extended definition by the extended definition uuid
	 *
	 * @param uuid the extended definition uuid
	 * @return the extended definition
	 * @throws org.openmrs.api.db.DAOException
	 *          when retrieving the extended definition failed
	 */
	ExtendedDefinition getExtendedDefinitionByUuid(final String uuid) throws DAOException;

	/**
	 * DAO method to get extended definition by the definition
	 *
	 * @param definition the definition
	 * @return the extended definition
	 * @throws org.openmrs.api.db.DAOException
	 *          when retrieving the extended definition failed
	 */
	ExtendedDefinition getExtendedDefinitionByDefinition(final CohortDefinition definition) throws DAOException;

	/**
	 * DAO method to get extended definition by the extended definition id
	 *
	 * @param id the extended definition id
	 * @return the extended definition
	 * @throws org.openmrs.api.db.DAOException
	 *          when retrieving the extended definition failed
	 */
	ExtendedDefinition getExtendedDefinition(final Integer id) throws DAOException;

	/**
	 * DAO method to get all saved extended definitions
	 *
	 * @return alll saved extended definitions
	 * @throws org.openmrs.api.db.DAOException
	 *          when retrieving all extended definitions failed
	 */
	List<ExtendedDefinition> getAllExtendedDefinition() throws DAOException;


	/**
	 * DAO method to save or update a definition property
	 *
	 * @param definitionProperty the definition property
	 * @return the saved definition property
	 * @throws org.openmrs.api.db.DAOException
	 *          when saving the definition property failed
	 */
	DefinitionProperty saveDefinitionProperty(final DefinitionProperty definitionProperty) throws DAOException;


	/**
	 * DAO method to get the definition property by the definition property id
	 *
	 * @param id the definition property id
	 * @return the definition property or null
	 * @throws org.openmrs.api.db.DAOException
	 *          when getting the definition property failed
	 */
	DefinitionProperty getDefinitionProperty(final Integer id) throws DAOException;

}
