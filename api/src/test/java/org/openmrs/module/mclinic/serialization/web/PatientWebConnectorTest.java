package org.openmrs.module.mclinic.serialization.web;

import org.junit.Test;
import org.openmrs.module.mclinic.serialization.serializer.openmrs.ObsSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PatientWebConnectorTest extends BaseModuleContextSensitiveTest {

    private static final String SERVER_URL = "http://localhost:8080/openmrs";

    @Test
    public void serialize_shouldDisplayAllPatientInformation() throws Exception {

        // compose url
        URL u = new URL(SERVER_URL + "/module/mclinic/download/patients.form");

        // setup http url connection
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.addRequestProperty("Content-type", "application/octet-stream");

        // write auth details to connection
        DataOutputStream outputStream = new DataOutputStream(new GZIPOutputStream(connection.getOutputStream()));
        outputStream.writeUTF("admin");
        outputStream.writeUTF("test");
        outputStream.writeBoolean(false);
        outputStream.writeInt(6);
        outputStream.writeInt(1);
        outputStream.close();

        DataInputStream inputStream = new DataInputStream(new GZIPInputStream(connection.getInputStream()));
        Integer responseStatus = inputStream.readInt();

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

        int count;
        byte[] buffer = new byte[1024];
        while ((count = inputStream.read(buffer)) > 0) {
            arrayOutputStream.write(buffer, 0, count);
        }
        arrayOutputStream.close();
        inputStream.close();

        File file = new File("/home/thaiya/mclinic.data");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(arrayOutputStream.toByteArray());
        fos.close();

        inputStream = new DataInputStream(new FileInputStream(file));

        if (responseStatus == HttpURLConnection.HTTP_OK) {

            // total number of patients
            Integer patientCounter = inputStream.readInt();
            System.out.println("Patient Counter: " + patientCounter);
            for (int j = 0; j < patientCounter; j++) {
                System.out.println("=================Patient=====================");
                System.out.println("Patient Id: " + inputStream.readInt());
                System.out.println("Family Name: " + inputStream.readUTF());
                System.out.println("Middle Name: " + inputStream.readUTF());
                System.out.println("Last Name: " + inputStream.readUTF());
                System.out.println("Gender: " + inputStream.readUTF());
                System.out.println("Birth Date: " + inputStream.readUTF());
                System.out.println("Identifier: " + inputStream.readUTF());
                System.out.println("Patients: " + j + " out of " + patientCounter);
            }

            Integer obsCounter = inputStream.readInt();
            System.out.println("Observation Counter: " + obsCounter);
            for (int j = 0; j < obsCounter; j++) {
                System.out.println("==================Observation=================");
                System.out.println("Patient Id: " + inputStream.readInt());
                System.out.println("Concept Name: " + inputStream.readUTF());

                byte type = inputStream.readByte();
                if (type == ObsSerializer.TYPE_STRING)
                    System.out.println("Value: " + inputStream.readUTF());
                else if (type == ObsSerializer.TYPE_INT)
                    System.out.println("Value: " + inputStream.readInt());
                else if (type == ObsSerializer.TYPE_DOUBLE)
                    System.out.println("Value: " + inputStream.readDouble());
                else if (type == ObsSerializer.TYPE_DATE)
                    System.out.println("Value: " + inputStream.readUTF());
                System.out.println("Time: " + inputStream.readUTF());
                System.out.println("Obs: " + j + " out of: " + obsCounter);
            }
            Integer formCounter = inputStream.readInt();
            System.out.println("Form Counter: " + formCounter);
            for (int j = 0; j < formCounter; j++) {
                System.out.println("==================Observation=================");
                System.out.println("Patient Id: " + inputStream.readInt());
                System.out.println("Concept Name: " + inputStream.readUTF());

                byte type = inputStream.readByte();
                if (type == ObsSerializer.TYPE_STRING)
                    System.out.println("Value: " + inputStream.readUTF());
                else if (type == ObsSerializer.TYPE_INT)
                    System.out.println("Value: " + inputStream.readInt());
                else if (type == ObsSerializer.TYPE_DOUBLE)
                    System.out.println("Value: " + inputStream.readDouble());
                else if (type == ObsSerializer.TYPE_DATE)
                    System.out.println("Value: " + inputStream.readUTF());
                System.out.println("Time: " + inputStream.readUTF());
                System.out.println("Form: " + j + " out of: " + formCounter);
            }
        }
        inputStream.close();
    }

}
