package org.openmrs.module.mclinic.serialization.serializer.openmrs;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.serialization.Serializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.HandlerUtil;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CohortSerializerTest extends BaseModuleContextSensitiveTest {

	@Test
	public void serialize_shouldSerializeCohortInformation() throws Exception {

		CohortService cohortService = Context.getCohortService();

		Cohort firstCohort = new Cohort();
		firstCohort.addMember(6);
		firstCohort.addMember(7);
		firstCohort.addMember(8);
		firstCohort.setName("First Cohort");
		firstCohort.setDescription("First cohort for testing the serializer");
		cohortService.saveCohort(firstCohort);

		Cohort secondCohort = new Cohort();
		secondCohort.addMember(6);
		secondCohort.addMember(7);
		secondCohort.addMember(8);
		secondCohort.setName("Second Cohort");
		secondCohort.setDescription("Second cohort for testing the serializer");
		cohortService.saveCohort(secondCohort);

		File file = File.createTempFile("CohortSerialization", "Example");
		GZIPOutputStream outputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		List<Cohort> cohorts = cohortService.getAllCohorts();
		Serializer serializer = HandlerUtil.getPreferredHandler(Serializer.class, List.class);
		serializer.write(outputStream, cohorts);

		outputStream.close();

		GZIPInputStream inputStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
		DataInputStream dataInputStream = new DataInputStream(inputStream);

		Integer cohortCounts = dataInputStream.readInt();
		System.out.println("Number of cohorts: " + cohortCounts);

		for (int i = 0; i < cohortCounts; i++) {
			System.out.println("Cohort ID: " + dataInputStream.readInt());
			System.out.println("Cohort Name: " + dataInputStream.readUTF());
		}

		inputStream.close();
	}

}
