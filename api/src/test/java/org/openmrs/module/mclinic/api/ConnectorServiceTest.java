package org.openmrs.module.mclinic.api;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests {@link MclinicService}.
 */
public class ConnectorServiceTest extends BaseModuleContextSensitiveTest {

	@Test
	public void getProgramConfiguration_shouldSaveProgramConfigurationIntoTheDatabase() {
		ProgramConfiguration programConfiguration = new ProgramConfiguration();
		programConfiguration.setName("{Program Configuration");
		programConfiguration.setDescription("Program Configuration Description");

		ConfiguredConcept configuredConcept = new ConfiguredConcept();
		configuredConcept.setConcept(Context.getConceptService().getConcept(20));
		configuredConcept.setConceptConfiguration(programConfiguration);

		programConfiguration.addConfiguredConcept(configuredConcept);

		Context.getService(MclinicService.class).saveProgramConfiguration(programConfiguration);
		Assert.assertNotNull(programConfiguration.getId());
		Assert.assertEquals("Program Configuration", programConfiguration.getName());
		Assert.assertEquals("Program Configuration Description", programConfiguration.getDescription());
		Assert.assertEquals(1, programConfiguration.getConfiguredConcepts().size());
	}

	@Test
	public void getProgramConfiguration_shouldReturnProgramConfigurationGivenAnId() {
		ProgramConfiguration programConfiguration = new ProgramConfiguration();
		programConfiguration.setName("Program Configuration");
		programConfiguration.setDescription("Program Configuration Description");

		ConfiguredConcept configuredConcept = new ConfiguredConcept();
		configuredConcept.setConcept(Context.getConceptService().getConcept(20));
		configuredConcept.setConceptConfiguration(programConfiguration);

		programConfiguration.addConfiguredConcept(configuredConcept);

		Context.getService(MclinicService.class).saveProgramConfiguration(programConfiguration);
		Assert.assertNotNull(programConfiguration.getId());
		Assert.assertEquals("Program Configuration", programConfiguration.getName());
		Assert.assertEquals("Program Configuration Description", programConfiguration.getDescription());
		Assert.assertEquals(1, programConfiguration.getConfiguredConcepts().size());

		Integer id = programConfiguration.getId();
		ProgramConfiguration savedConceptConfiguration = Context.getService(MclinicService.class).getProgramConfiguration(id);
		Assert.assertNotNull(savedConceptConfiguration);
		Assert.assertEquals("Program Configuration", programConfiguration.getName());
		Assert.assertEquals("Program Configuration Description", programConfiguration.getDescription());
		Assert.assertEquals(1, programConfiguration.getConfiguredConcepts().size());
	}
}
