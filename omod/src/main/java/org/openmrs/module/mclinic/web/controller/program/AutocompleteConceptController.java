package org.openmrs.module.mclinic.web.controller.program;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
@Controller
@RequestMapping("/module/mclinic/search/concept")
public class AutocompleteConceptController {

	@RequestMapping(method = RequestMethod.GET)
	public void search(final @RequestParam(required = true, value = "searchTerm") String searchTerm,
	                   final HttpServletResponse response) throws Exception {
		List<Concept> concepts = Context.getConceptService().getConceptsByName(searchTerm);
		OutputStream stream = response.getOutputStream();

		JsonFactory f = new JsonFactory();
		JsonGenerator g = f.createJsonGenerator(stream, JsonEncoding.UTF8);
		g.useDefaultPrettyPrinter();

		g.writeStartObject();
		g.writeArrayFieldStart("elements");

		for (Concept concept : concepts) {
			g.writeStartObject();
			g.writeStringField("uuid", concept.getUuid());
			g.writeStringField("name", concept.getDisplayString());
			g.writeEndObject();
		}
		g.writeEndArray();
		g.writeEndObject();

		g.close();
	}
}
