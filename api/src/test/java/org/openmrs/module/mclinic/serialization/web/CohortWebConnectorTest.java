package org.openmrs.module.mclinic.serialization.web;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CohortWebConnectorTest extends BaseModuleContextSensitiveTest {

	private static final String SERVER_URL = "http://localhost:8080/openmrs";

	@Test
	public void serialize_shouldDisplayAllCohortInformation() throws Exception {

		// compose url
		URL u = new URL(SERVER_URL + "/module/mclinic/download/cohort.form");

		// setup http url connection
		HttpURLConnection connection = (HttpURLConnection) u.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(8000);
		connection.setReadTimeout(8000);
		connection.addRequestProperty("Content-type", "application/octet-stream");

		// write auth details to connection
		DataOutputStream outputStream = new DataOutputStream(new GZIPOutputStream(connection.getOutputStream()));
		outputStream.writeUTF("admin");
		outputStream.writeUTF("test");
		outputStream.writeBoolean(false);
		outputStream.close();

		DataInputStream inputStream = new DataInputStream(new GZIPInputStream(connection.getInputStream()));
		Integer responseStatus = inputStream.readInt();

		if (responseStatus == HttpURLConnection.HTTP_OK) {

			Integer cohortCounts = inputStream.readInt();
			System.out.println("Number of cohorts: " + cohortCounts);

			for (int i = 0; i < cohortCounts; i++) {
				System.out.println("Cohort ID: " + inputStream.readInt());
				System.out.println("Cohort Name: " + inputStream.readUTF());
			}
		}
		inputStream.close();
	}

}
