package org.openmrs.module.mclinic.serialization;

import java.io.InputStream;
import java.io.OutputStream;

public interface Processor {

	/**
	 * Process any stream connection to this module
	 *
	 * @param inputStream  the input stream
	 * @param outputStream the output stream
	 * @throws Exception when the stream processing failed
	 */
	void process(final InputStream inputStream, final OutputStream outputStream) throws Exception;
}
