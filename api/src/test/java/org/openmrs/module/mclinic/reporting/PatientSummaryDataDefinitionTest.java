package org.openmrs.module.mclinic.reporting;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.converter.*;
import org.openmrs.module.reporting.data.encounter.definition.*;
import org.openmrs.module.reporting.data.obs.definition.ObsDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasePatientEncounterQuery;
import org.openmrs.module.reporting.query.obs.definition.DateObsQuery;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PatientSummaryDataDefinitionTest extends BaseModuleContextSensitiveTest {

    @Before
    public void setup() throws Exception {
        executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
    }

    private EvaluationContext prepareEvaluationContext() {
        PatientService patientService = Context.getPatientService();
        EncounterService encounterService = Context.getEncounterService();

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("currentDate", new Date());
        context.addParameterValue("identifierTypes",
                Arrays.asList(patientService.getPatientIdentifierType(1), patientService.getPatientIdentifierType(2)));
        context.addParameterValue("encounterTypes",
                Arrays.asList(encounterService.getEncounterType(1), encounterService.getEncounterType(2), encounterService.getEncounterType(6)));

        return context;
    }

    @Test
    public void generateSummary() throws Exception {

        PatientDataSetDefinition definition = new PatientDataSetDefinition();

        definition.addColumn("id", new PatientIdDataDefinition(), StringUtils.EMPTY, new ObjectFormatter());

        ListConverter listConverter = new ListConverter();
        listConverter.setMaxNumberOfItems(1);

        PatientIdentifierDataDefinition preferredIdentifier = new PatientIdentifierDataDefinition();
        preferredIdentifier.addParameter(new Parameter("types", "identifier types", PatientIdentifier.class));
        definition.addColumn("identifier", preferredIdentifier, "types=${identifierTypes}", listConverter);

        definition.addColumn("name", new PreferredNameDataDefinition(), StringUtils.EMPTY, new ObjectFormatter("{familyName}, {givenName}"));

        AgeDataDefinition ageOnDate = new AgeDataDefinition();
        ageOnDate.addParameter(new Parameter("effectiveDate", "effective date", Date.class));
        definition.addColumn("age", ageOnDate, "effectiveDate=${currentDate}", new AgeConverter());

        definition.addColumn("birthdate", new BirthdateDataDefinition(), StringUtils.EMPTY, new BirthdateConverter("dd-MMM-yyyy"));
        definition.addColumn("gender", new GenderDataDefinition(), StringUtils.EMPTY, new ObjectFormatter());

        DateObsQuery returnVisitQuery = new DateObsQuery();
        returnVisitQuery.setTimeModifier(PatientSetService.TimeModifier.LAST);
        returnVisitQuery.addConcept(Context.getConceptService().getConcept(20));

        ObsDataSetDefinition returnVisit = new ObsDataSetDefinition();
        returnVisit.addRowFilter(returnVisitQuery, null);
        returnVisit.addColumn("return-visit-date", new ObsDatetimeDataDefinition(), null, new DateConverter("dd-MMM-yyyy"));
        definition.addColumns("return-visit", returnVisit, null);

        BasePatientEncounterQuery recentEncounterQuery = new BasePatientEncounterQuery();
        recentEncounterQuery.setTimeModifier(PatientSetService.TimeModifier.LAST);
        recentEncounterQuery.addParameter(new Parameter("encounterTypes", "encounter types", List.class));

        EncounterDataSetDefinition recentEncounterDataSet = new EncounterDataSetDefinition();
        recentEncounterDataSet.addRowFilter(recentEncounterQuery, "encounterTypes=${encounterTypes}");
        recentEncounterDataSet.addColumn("last-encounter-id", new EncounterIdDataDefinition(), null, new ObjectFormatter());
        recentEncounterDataSet.addColumn("last-encounter-type", new EncounterTypeDataDefinition(), null, new ObjectFormatter());
        recentEncounterDataSet.addColumn("last-encounter-location", new EncounterLocationDataDefinition(), null, new ObjectFormatter());
        recentEncounterDataSet.addColumn("last-encounter-provider", new EncounterProviderDataDefinition(), null, new PropertyConverter(PersonName.class, "personName"), new ObjectFormatter("{familyName}, {givenName}"));
        recentEncounterDataSet.addColumn("last-encounter-provider-age", new EncounterProviderDataDefinition(), null, new PropertyConverter(Age.class, "age"), new ObjectFormatter());
        recentEncounterDataSet.addColumn("last-encounter-date", new EncounterDatetimeDataDefinition(), null, new DateConverter("dd-MMM-yyyy"));

        definition.addColumns("last-visit", recentEncounterDataSet, null);

        BasePatientEncounterQuery firstEncounterQuery = new BasePatientEncounterQuery();
        firstEncounterQuery.setTimeModifier(PatientSetService.TimeModifier.FIRST);
        firstEncounterQuery.addParameter(new Parameter("encounterTypes", "encounter types", List.class));

        EncounterDataSetDefinition firstEncounterDataSet = new EncounterDataSetDefinition();
        firstEncounterDataSet.addRowFilter(firstEncounterQuery, "encounterTypes=${encounterTypes}");
        firstEncounterDataSet.addColumn("first-encounter-id", new EncounterIdDataDefinition(), null, new ObjectFormatter());
        firstEncounterDataSet.addColumn("first-encounter-type", new EncounterTypeDataDefinition(), null, new ObjectFormatter());
        firstEncounterDataSet.addColumn("first-encounter-date", new EncounterDatetimeDataDefinition(), null, new DateConverter("dd-MMM-yyyy"));

        definition.addColumns("first-visit", firstEncounterDataSet, null);

        SimpleDataSet dataset = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(definition, prepareEvaluationContext());
        DataSetUtil.printDataSet(dataset, System.out);
    }
}
