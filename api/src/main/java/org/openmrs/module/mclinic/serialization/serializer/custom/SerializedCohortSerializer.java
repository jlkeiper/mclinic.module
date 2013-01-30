package org.openmrs.module.mclinic.serialization.serializer.custom;

import org.openmrs.annotation.Handler;
import org.openmrs.module.mclinic.serialization.Serializer;
import org.openmrs.module.mclinic.serialization.serializable.SerializedCohort;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Handler(supports = SerializedCohort.class, order = 50)
public class SerializedCohortSerializer implements Serializer {

	/**
	 * Write the data to the output stream.
	 *
	 * @param stream the output stream
	 * @param data   the data that need to be written to the output stream
	 */
	@Override
	public void write(final OutputStream stream, final Object data) throws IOException {

		SerializedCohort cohort = (SerializedCohort) data;

		DataOutputStream outputStream = new DataOutputStream(stream);

		outputStream.writeInt(cohort.getId());
		outputStream.writeUTF(cohort.getName());
        outputStream.flush();
	}
}
