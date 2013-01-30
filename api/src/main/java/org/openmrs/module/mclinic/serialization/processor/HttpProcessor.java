package org.openmrs.module.mclinic.serialization.processor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.cohort.CohortSearchHistory;
import org.openmrs.module.mclinic.api.ProgramConfiguration;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.MclinicUtil;
import org.openmrs.module.mclinic.reporting.metadata.DefinitionProperty;
import org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition;
import org.openmrs.module.mclinic.reporting.service.ReportingConnectorService;
import org.openmrs.module.mclinic.serialization.Processor;
import org.openmrs.module.mclinic.serialization.Serializer;
import org.openmrs.module.mclinic.serialization.serializable.SerializedCohort;
import org.openmrs.module.mclinic.serialization.serializable.SerializedForm;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.PatientSearch;
import org.openmrs.reporting.PatientSearchReportObject;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsConstants;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("deprecation")
public class HttpProcessor implements Processor {
	private static final Log log = LogFactory.getLog(HttpProcessor.class);
	public static final String PROCESS_COHORT = "download.cohort";
	public static final String PROCESS_PATIENTS = "download.patients";
	public static final String SEARCH_PATIENT = "search.patient";
	private String action;

	public HttpProcessor(String action) {
		this.action = action;
	}
	
	/**
	 * Get the http processor action
	 *
	 * @return the http processor action
	 */
	public String getAction() {
		return this.action;
	}
	
	/**
	 * Set the http processor action
	 *
	 * @param action the http processor action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Process any stream connection to this module
	 *
	 * @param inputStream  the input stream
	 * @param outputStream the output stream
	 * @throws Exception when the stream processing failed
	 */
	@Override
	public void process(InputStream inputStream, OutputStream outputStream) throws Exception {
		GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(inputStream));

		DataInputStream dataInputStream = new DataInputStream(gzipInputStream);
		String username = dataInputStream.readUTF();
		String password = dataInputStream.readUTF();
		Boolean savedSearch = false;
		if (!StringUtils.equalsIgnoreCase(getAction(), HttpProcessor.SEARCH_PATIENT))
			savedSearch = dataInputStream.readBoolean();
		
		Integer cohortId = 0;
		Integer programId = 0;
		String patientName = null;
		String identifier = null;
		
		if (StringUtils.equalsIgnoreCase(getAction(), HttpProcessor.PROCESS_PATIENTS)) {
			cohortId = dataInputStream.readInt();
			programId = dataInputStream.readInt();
		}

		if (StringUtils.equalsIgnoreCase(getAction(), HttpProcessor.SEARCH_PATIENT)) {
			programId = dataInputStream.readInt();
			patientName = dataInputStream.readUTF();
			identifier = dataInputStream.readUTF();
		}
		dataInputStream.close();

		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new BufferedOutputStream(outputStream));
		DataOutputStream dataOutputStream = new DataOutputStream(gzipOutputStream);
		try {
			Context.openSession();
			Context.authenticate(username, password);
			dataOutputStream.writeInt(HttpURLConnection.HTTP_OK);
			Serializer serializer = HandlerUtil.getPreferredHandler(Serializer.class, List.class);

			if (StringUtils.equalsIgnoreCase(getAction(), HttpProcessor.PROCESS_COHORT))
				downloadCohorts(dataOutputStream, savedSearch, serializer);

			if (StringUtils.equalsIgnoreCase(getAction(), HttpProcessor.PROCESS_PATIENTS))
				downloadPatients(dataOutputStream, savedSearch, serializer, cohortId, programId);
			
			if (StringUtils.equalsIgnoreCase(getAction(), HttpProcessor.SEARCH_PATIENT))
				searchPatient(dataOutputStream, serializer, programId, patientName, identifier);
			
			dataOutputStream.close();
		} catch (Exception e) {
			log.error("Processing stream failed!", e);
			dataOutputStream.writeInt(HttpURLConnection.HTTP_UNAUTHORIZED);
			dataOutputStream.close();
		} finally {
			Context.closeSession();
		}
	}

	private void downloadPatients(DataOutputStream dataOutputStream,
			boolean savedSearch, Serializer serializer, Integer cohortId,
			Integer programId) throws Exception {
		MclinicService mclinicService = (MclinicService) Context.getService(MclinicService.class);

		Cohort cohort = new Cohort();
		if (savedSearch) {
			CohortSearchHistory history = new CohortSearchHistory();
			PatientSearchReportObject patientSearchReportObject = (PatientSearchReportObject) Context.getReportObjectService().getReportObject(cohortId);
			if (patientSearchReportObject != null) {
				history.addSearchItem(PatientSearch.createSavedSearchReference(cohortId.intValue()));
				cohort = history.getPatientSet(0, null);
			}
		} else
			cohort = Context.getCohortService().getCohort(cohortId);

		log.debug("Streaming patients information!");
		serializer.write(dataOutputStream, mclinicService.getCohortPatients(cohort));
		
		// check the concept list
		Collection<Concept> concepts = null;
		ProgramConfiguration programConfiguration = mclinicService.getProgramConfiguration(programId);
		if (programConfiguration != null)
			concepts = MclinicUtil.getConcepts(programConfiguration.getConfiguredConcepts());
		
		log.debug("Streaming observations information!");
		serializer.write(dataOutputStream, mclinicService.getCohortObservations(cohort, concepts));
		
		// evaluate and get the applicable form for the patients
		CohortDefinitionService cohortDefinitionService = (CohortDefinitionService) Context.getService(CohortDefinitionService.class);
		ReportingConnectorService reportingConnectorService = (ReportingConnectorService) Context.getService(ReportingConnectorService.class);
		List<ExtendedDefinition> definitions = reportingConnectorService.getAllExtendedDefinition();

		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(cohort);

		Collection<?> intersectedMemberIds = Collections.emptyList();
		List<SerializedForm> serializedForms = new ArrayList<SerializedForm>();
		for (ExtendedDefinition definition : definitions)
			if (definition.containsProperty(ExtendedDefinition.DEFINITION_PROPERTY_FORM)) {
				EvaluatedCohort evaluatedCohort = cohortDefinitionService.evaluate(definition.getCohortDefinition(), context);
				
				// the cohort could be null, so we don't want to get exception during the intersection process
				if (cohort != null)
					intersectedMemberIds = CollectionUtils.intersection(cohort.getMemberIds(), evaluatedCohort.getMemberIds());
				for (DefinitionProperty definitionProperty : definition.getProperties()) {
					Integer formId = NumberUtils.toInt(definitionProperty.getPropertyValue());
					for (Object patientId : intersectedMemberIds)
						serializedForms.add(new SerializedForm(NumberUtils.toInt(String.valueOf(patientId)), formId));
				}
			}
		log.debug("Streaming forms information!");
		serializer.write(dataOutputStream, serializedForms);
	}

	private void downloadCohorts(DataOutputStream dataOutputStream, boolean savedSearch, Serializer serializer) throws IOException {
		if (savedSearch) {
			List<SerializedCohort> serializedCohorts = new ArrayList<SerializedCohort>();
			List<AbstractReportObject> objects = Context.getReportObjectService().getReportObjectsByType(OpenmrsConstants.REPORT_OBJECT_TYPE_PATIENTSEARCH);
			for (AbstractReportObject object : objects) {
				SerializedCohort serializedCohort = new SerializedCohort();
				serializedCohort.setId(object.getReportObjectId());
				serializedCohort.setName(object.getName());
				serializedCohorts.add(serializedCohort);
			}
			serializer.write(dataOutputStream, serializedCohorts);
		} else 
			serializer.write(dataOutputStream, Context.getCohortService().getAllCohorts());
	}

	public static void searchPatient(DataOutputStream dataOutputStream,
			Serializer serializer, Integer programId, String patientName, String identifier) throws IOException {
		if ((patientName != null) && (patientName.trim().length() == 0))
			patientName = null;
		if ((identifier != null) && (identifier.trim().length() == 0)) {
			identifier = null;
		}

		List<Patient> patients = Context.getPatientService().getPatients(patientName, identifier, null, false);
		log.debug("Streaming patients information!");
		serializer.write(dataOutputStream, patients);

		Collection<Concept> concepts = null;
		MclinicService mclinicService = (MclinicService) Context.getService(MclinicService.class);
		ProgramConfiguration programConfiguration = mclinicService.getProgramConfiguration(programId);
		if (programConfiguration != null)
			concepts = MclinicUtil.getConcepts(programConfiguration.getConfiguredConcepts());
		log.debug("Streaming observations information!");

		serializer.write(dataOutputStream, mclinicService.getCohortObservations(new Cohort(patients), concepts));
	}
}