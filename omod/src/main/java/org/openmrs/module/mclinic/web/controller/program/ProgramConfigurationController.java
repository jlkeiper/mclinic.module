package org.openmrs.module.mclinic.web.controller.program;

import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.ProgramConfiguration;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProgramConfigurationController {

	@RequestMapping(value = "/module/mclinic/program/programConfiguration", method = RequestMethod.GET)
	public void preparePage(final @RequestParam(value = "uuid", required = false) String uuid,
	                        final Model model) {
		MclinicService service = Context.getService(MclinicService.class);
		ProgramConfiguration programConfiguration = service.getProgramConfigurationByUuid(uuid);
		model.addAttribute("configuration", programConfiguration);
	}

	@RequestMapping(value = "/module/mclinic/program/programConfiguration", method = RequestMethod.POST)
	public String process(final @RequestParam(value = "name", required = true) String name,
	                      final @RequestParam(value = "description", required = true) String description,
	                      final @RequestParam(value = "configurationUuid", required = false) String configurationUuid) {
		MclinicService service = Context.getService(MclinicService.class);

		ProgramConfiguration programConfiguration = service.getProgramConfigurationByUuid(configurationUuid);
		if (programConfiguration == null)
			programConfiguration = new ProgramConfiguration();
		programConfiguration.setName(name);
		programConfiguration.setDescription(description);

		service.saveProgramConfiguration(programConfiguration);

		return "redirect:manageProgram.form?uuid=" + programConfiguration.getUuid();
	}

	@RequestMapping(value = "/module/mclinic/program/deleteConfiguration", method = RequestMethod.GET)
	public
	@ResponseBody
	Boolean delete(final @RequestParam(value = "uuid", required = false) String uuid) {
		Boolean deleted =false;
		MclinicService service = Context.getService(MclinicService.class);
		ProgramConfiguration programConfiguration = service.getProgramConfigurationByUuid(uuid);
		if (programConfiguration != null) {
			programConfiguration.setRetired(true);
			service.saveProgramConfiguration(programConfiguration);
			deleted = true;
		}
		return deleted;
	}
}