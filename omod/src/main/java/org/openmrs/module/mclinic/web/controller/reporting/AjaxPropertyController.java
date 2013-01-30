package org.openmrs.module.mclinic.web.controller.reporting;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.reporting.metadata.DefinitionProperty;
import org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition;
import org.openmrs.module.mclinic.reporting.service.ReportingConnectorService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AjaxPropertyController {

	@RequestMapping(value = "/module/mclinic/reporting/saveProperty.form", method = RequestMethod.POST)
	public void processSave(final @RequestParam(value = "id", required = true) Integer id,
	                        final @RequestParam(value = "property", required = true) String property,
	                        final @RequestParam(value = "value", required = true) String value) throws Exception {
		ReportingConnectorService reportingConnectorService = Context.getService(ReportingConnectorService.class);
		// search the definition property
		DefinitionProperty definitionProperty = reportingConnectorService.getDefinitionProperty(id);
		// update the definition property and then save it
		BeanUtils.setProperty(definitionProperty, property, value);
		reportingConnectorService.saveDefinitionProperty(definitionProperty);
	}

	@RequestMapping(value = "/module/mclinic/reporting/newProperty.form", method = RequestMethod.POST)
	public void processNew(final @RequestParam(value = "uuid", required = true) String uuid,
	                       final @RequestParam(value = "property", required = true) String property,
	                       final @RequestParam(value = "propertyValue", required = true) String propertyValue,
	                       final @RequestParam(value = "propertyDescription", required = true) String propertyDescription) throws Exception {

		CohortDefinitionService definitionService = Context.getService(CohortDefinitionService.class);
		ReportingConnectorService reportingConnectorService = Context.getService(ReportingConnectorService.class);
		// create the new definition property
		DefinitionProperty definitionProperty = new DefinitionProperty(property, propertyValue, propertyDescription);
		// check if the extended definition for this cohort definition already exist or not
		CohortDefinition definition = definitionService.getDefinitionByUuid(uuid);
		ExtendedDefinition extendedDefinition = reportingConnectorService.getExtendedDefinitionByDefinition(definition);
		if (extendedDefinition == null) {
			extendedDefinition = new ExtendedDefinition();
			extendedDefinition.setCohortDefinition(definition);
		}
		extendedDefinition.addDefinitionProperty(definitionProperty);
		reportingConnectorService.saveExtendedDefinition(extendedDefinition);
	}

	@RequestMapping(value = "/module/mclinic/reporting/searchProperty.form", method = RequestMethod.POST)
	public void processSearch(final @RequestParam(value = "uuid", required = true) String uuid,
	                          final HttpServletResponse response) throws IOException {
		CohortDefinitionService definitionService = Context.getService(CohortDefinitionService.class);
		ReportingConnectorService reportingConnectorService = Context.getService(ReportingConnectorService.class);

		CohortDefinition definition = definitionService.getDefinitionByUuid(uuid);
		ExtendedDefinition extendedDefinition = reportingConnectorService.getExtendedDefinitionByDefinition(definition);

		OutputStream stream = response.getOutputStream();

		JsonFactory f = new JsonFactory();
		JsonGenerator g = f.createJsonGenerator(stream, JsonEncoding.UTF8);
		g.useDefaultPrettyPrinter();

		g.writeStartArray();
		if (extendedDefinition != null)
			for (DefinitionProperty property : extendedDefinition.getProperties()) {
				g.writeStartObject();
				g.writeNumberField("propertyId", property.getId());
				g.writeStringField("property", property.getProperty());
				g.writeStringField("propertyValue", property.getPropertyValue());
				g.writeStringField("propertyDescription", property.getPropertyDescription());
				g.writeEndObject();
			}
		g.writeEndArray();

		g.close();
	}
}