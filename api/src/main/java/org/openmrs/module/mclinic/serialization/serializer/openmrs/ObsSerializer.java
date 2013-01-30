package org.openmrs.module.mclinic.serialization.serializer.openmrs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.serialization.Serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


@Handler(supports = Obs.class, order = 50)
public class ObsSerializer implements Serializer {

	private static final Log log = LogFactory.getLog(ObsSerializer.class);

	public static final Integer TYPE_STRING = 1;

	public static final Integer TYPE_INT = 2;

	public static final Integer TYPE_DOUBLE = 3;

	public static final Integer TYPE_DATE = 4;

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
            Obs obs = (Obs) data;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DataOutputStream outputStream = new DataOutputStream(stream);
            // write the person id of the observation
            outputStream.writeInt(obs.getPersonId());
            // write the concept name of the observation
            outputStream.writeUTF(obs.getConcept().getDisplayString());
            // write the data type and the value of the observation
            if (obs.getValueDatetime() != null) {
                outputStream.writeByte(TYPE_DATE);
                outputStream.writeUTF(dateFormat.format(obs.getValueDatetime()));
            } else if (obs.getValueCoded() != null) {
                outputStream.writeByte(TYPE_STRING);
                outputStream.writeUTF(obs.getValueCoded().getDisplayString());
            } else if (obs.getValueNumeric() != null) {
                outputStream.writeByte(TYPE_DOUBLE);
                outputStream.writeDouble(obs.getValueNumeric());
            } else {
                outputStream.writeByte(TYPE_STRING);
                outputStream.writeUTF(obs.getValueAsString(Context.getLocale()));
            }
            // write the datetime of the observation
            outputStream.writeUTF(dateFormat.format(obs.getObsDatetime()));
        } catch (IOException e) {
            log.info("Writing obs information failed!", e);
        }
    }
}
