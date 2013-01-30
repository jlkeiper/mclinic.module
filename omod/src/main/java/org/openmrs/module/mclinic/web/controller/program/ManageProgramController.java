package org.openmrs.module.mclinic.web.controller.program;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.ProgramConfiguration;
import org.openmrs.module.mclinic.api.ConfiguredConcept;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageProgramController {

    @RequestMapping(value = "/module/mclinic/program/manageProgram", method = RequestMethod.GET)
    public void preparePage(final @RequestParam(value = "uuid", required = true) String uuid,
                            final Model model) {
        MclinicService service = Context.getService(MclinicService.class);
        ProgramConfiguration programConfiguration = service.getProgramConfigurationByUuid(uuid);
        Set<Concept> concepts = MclinicUtil.getConcepts(programConfiguration.getConfiguredConcepts());
        Set<String> conceptUuids = MclinicUtil.getConceptUuids(concepts);

        model.addAttribute("configuration", programConfiguration);
        model.addAttribute("concepts", concepts);
        model.addAttribute("conceptUuids", MclinicUtil.convertString(conceptUuids));
    }

    @RequestMapping(value = "/module/mclinic/program/manageProgram", method = RequestMethod.POST)
    public void process(final @RequestParam(value = "conceptUuids", required = true) String conceptUuids,
                        final @RequestParam(value = "configurationUuid", required = true) String configurationUuid,
                        final Model model, final HttpServletRequest request) {

        MclinicService service = Context.getService(MclinicService.class);
        ProgramConfiguration programConfiguration = service.getProgramConfigurationByUuid(configurationUuid);

        // the uuids coming from the web page. might contains new uuid and will not contains retired uuid
        Set<String> createdConceptUuidValues = new LinkedHashSet<String>(Arrays.asList(StringUtils.split(StringUtils.defaultString(conceptUuids), ",")));
        // the saved uuids. might contains retired uuid and will not contains new uuid
        Set<String> savedConceptUuidValues = new LinkedHashSet<String>();
        for (ConfiguredConcept configuredConcept : programConfiguration.getConfiguredConcepts()) {
            if (!configuredConcept.isRetired()) {
                Concept concept = configuredConcept.getConcept();
                savedConceptUuidValues.add(concept.getUuid());
            }
        }

        Collection<?> intersectedUuids = CollectionUtils.intersection(createdConceptUuidValues, savedConceptUuidValues);
        Collection<?> retiredConceptUuids = CollectionUtils.subtract(savedConceptUuidValues, intersectedUuids);
        Collection<?> createdConceptUuids = CollectionUtils.subtract(createdConceptUuidValues, intersectedUuids);

        for (ConfiguredConcept configuredConcept : programConfiguration.getConfiguredConcepts()) {
            Concept concept = configuredConcept.getConcept();
            if (retiredConceptUuids.contains(concept.getUuid())) {
                configuredConcept.setRetired(Boolean.TRUE);
                configuredConcept.setRetiredBy(Context.getAuthenticatedUser());
                configuredConcept.setDateRetired(new Date());
            }
        }

        for (Object conceptUuid : createdConceptUuids) {
            Concept concept = Context.getConceptService().getConceptByUuid(String.valueOf(conceptUuid));
            if (concept != null) {
                ConfiguredConcept configuredConcept = new ConfiguredConcept();
                configuredConcept.setConcept(concept);
                configuredConcept.setConceptConfiguration(programConfiguration);
                programConfiguration.addConfiguredConcept(configuredConcept);
            }
        }
        service.saveProgramConfiguration(programConfiguration);

        Set<Concept> concepts = MclinicUtil.getConcepts(programConfiguration.getConfiguredConcepts());
        model.addAttribute("configuration", programConfiguration);
        model.addAttribute("concepts", concepts);
        model.addAttribute("conceptUuids", MclinicUtil.convertString(MclinicUtil.getConceptUuids(concepts)));
    }
}