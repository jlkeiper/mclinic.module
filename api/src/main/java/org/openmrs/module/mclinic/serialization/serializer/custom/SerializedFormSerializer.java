package org.openmrs.module.mclinic.serialization.serializer.custom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.mclinic.reporting.metadata.ExtendedDefinition;
import org.openmrs.module.mclinic.serialization.Serializer;
import org.openmrs.module.mclinic.serialization.serializable.SerializedForm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Handler(supports = SerializedForm.class, order = 50)
public class SerializedFormSerializer implements Serializer {

	private static final Log log = LogFactory.getLog(SerializedFormSerializer.class);

	public static final Integer TYPE_INT = 2;

	/**
	 * Write the data to the output stream.
	 *
	 * @param stream the output stream
	 * @param data   the data that need to be written to the output stream
	 * @throws java.io.IOException thrown when the writing process encounter is failing
	 */
	@Override
	public void write(final OutputStream stream, final Object data) throws IOException {
        try {
            SerializedForm serializedForm = (SerializedForm) data;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DataOutputStream outputStream = new DataOutputStream(stream);

            outputStream.writeInt(serializedForm.getPatientId());
            outputStream.writeUTF(ExtendedDefinition.DEFINITION_PROPERTY_FORM);
            outputStream.writeByte(TYPE_INT);
            outputStream.writeInt(serializedForm.getFormId());
            outputStream.writeUTF(dateFormat.format(new Date()));
        } catch (IOException e) {
            log.info("Writing form information failed!", e);
        }
    }
}
