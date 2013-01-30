package org.openmrs.module.mclinic.serialization.serializer.openmrs;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.mclinic.serialization.Serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Handler(supports = Cohort.class, order = 50)
public class CohortSerializer implements Serializer {

	/**
	 * Write the data to the output stream.
	 *
	 * @param stream the output stream
	 * @param data   the data that need to be written to the output stream
	 */
	@Override
	public void write(final OutputStream stream, final Object data) throws IOException {

		Cohort cohort = (Cohort) data;

		DataOutputStream outputStream = new DataOutputStream(stream);

		outputStream.writeInt(cohort.getCohortId());
		outputStream.writeUTF(cohort.getName());
        outputStream.flush();
	}
}
