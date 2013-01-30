package org.openmrs.module.mclinic.web.controller.form;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for properties page jsp
 * 
 * @author Samuel Mbugua
 */
@Controller
public class PropsPageController {
	
	@RequestMapping(value="/module/mclinic/propertiesPage", method=RequestMethod.GET)
	public Map<String, Object> populateForm() {
		MclinicService mhs = (MclinicService)Context.getService(MclinicService.class);
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("systemVars", mhs.getSystemVariables());
		return map;
	}
}
