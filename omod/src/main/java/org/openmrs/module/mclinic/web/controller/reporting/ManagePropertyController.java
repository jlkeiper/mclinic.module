package org.openmrs.module.mclinic.web.controller.reporting;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/module/mclinic/reporting/manageProperty.form")
public class ManagePropertyController {

	@RequestMapping(method = RequestMethod.GET)
	public void preparePage(final Model model) {
		CohortDefinitionService definitionService = Context.getService(CohortDefinitionService.class);
		model.addAttribute("cohortDefinitions", definitionService.getAllDefinitions(Boolean.FALSE));
	}

}
