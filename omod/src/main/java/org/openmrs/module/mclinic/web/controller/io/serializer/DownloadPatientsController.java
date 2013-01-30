package org.openmrs.module.mclinic.web.controller.io.serializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.mclinic.serialization.Processor;
import org.openmrs.module.mclinic.serialization.processor.HttpProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/module/mclinic/download/patients")
public class DownloadPatientsController {

	@RequestMapping(method = RequestMethod.POST)
	public void process(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		Processor processor = new HttpProcessor(HttpProcessor.PROCESS_PATIENTS);
		processor.process(request.getInputStream(), response.getOutputStream());
	}

}
