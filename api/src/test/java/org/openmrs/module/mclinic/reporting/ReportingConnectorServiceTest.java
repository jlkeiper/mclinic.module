package org.openmrs.module.mclinic.reporting;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.reporting.metadata.DefinitionProperty;
import org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition;
import org.openmrs.module.mclinic.reporting.service.ReportingConnectorService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ReportingConnectorServiceTest extends BaseModuleContextSensitiveTest {

	@Test
	public void saveExtendedDefinition_shouldSaveExtendedDefinitionIntoTheDatabase() throws Exception {

		CohortDefinitionService definitionService = Context.getService(CohortDefinitionService.class);

		CohortDefinition cohortDefinition = new EncounterCohortDefinition();
		cohortDefinition.setName("Cohort Definition");
		cohortDefinition.setDescription("Cohort Definition Description");
		CohortDefinition savedDefinition = definitionService.saveDefinition(cohortDefinition);
		Assert.assertNotNull(savedDefinition.getUuid());

		ReportingConnectorService reportingConnectorService = Context.getService(ReportingConnectorService.class);
		// create the new definition property

		String property = "example.property";
		String propertyValue = "Example Property";
		String propertyDescription = "Example Property Description";

		DefinitionProperty definitionProperty = new DefinitionProperty(property, propertyValue, propertyDescription);
		// check if the extended definition for this cohort definition already exist or not
		CohortDefinition definition = definitionService.getDefinitionByUuid(savedDefinition.getUuid());
		ExtendedDefinition extendedDefinition = reportingConnectorService.getExtendedDefinitionByDefinition(definition);
		if (extendedDefinition == null) {
			extendedDefinition = new ExtendedDefinition();
			extendedDefinition.setCohortDefinition(definition);
		}
		extendedDefinition.addDefinitionProperty(definitionProperty);
		reportingConnectorService.saveExtendedDefinition(extendedDefinition);
	}

}
