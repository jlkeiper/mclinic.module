package org.openmrs.module.mclinic.web.controller.io.serializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.mclinic.serialization.Processor;
import org.openmrs.module.mclinic.serialization.processor.HttpProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/module/mclinic/search/patient")
public class SearchPatientsController
{
  @RequestMapping(method=RequestMethod.POST)
  public void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Processor processor = new HttpProcessor(HttpProcessor.SEARCH_PATIENT);
    processor.process(request.getInputStream(), response.getOutputStream());
  }
}