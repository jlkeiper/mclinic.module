package org.openmrs.module.mclinic.serialization;

import java.io.IOException;
import java.io.OutputStream;

public interface Serializer {

	int ZERO = 0;

	/**
	 * Write the data to the output stream.
	 *
	 * @param stream the output stream
	 * @param data   the data that need to be written to the output stream
	 * @throws java.io.IOException thrown when the writing process encounter is failing
	 */
	void write(final OutputStream stream, final Object data) throws IOException;

}
