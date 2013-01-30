package org.openmrs.module.mclinic.serialization.serializer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.module.mclinic.serialization.Serializer;
import org.openmrs.util.HandlerUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Handler(supports = List.class, order = 50)
public class ListSerializer implements Serializer {

	/**
	 * Write the data to the output stream.
	 *
	 * @param stream the output stream
	 * @param data   the data that need to be written to the output stream
	 * @throws java.io.IOException thrown when the writing process encounter is failing
	 */
	@Override
	public void write(final OutputStream stream, final Object data) throws IOException {

		DataOutputStream outputStream = new DataOutputStream(stream);

		List<?> list = null;
		if (ClassUtils.isAssignable(data.getClass(), List.class))
			list = (List<?>) data;

		if (list == null || CollectionUtils.isEmpty(list))
			outputStream.writeInt(Serializer.ZERO);
		else {
			outputStream.writeInt(list.size());
			for (Object object : list) {
				Serializer serializer = HandlerUtil.getPreferredHandler(Serializer.class, object.getClass());
				serializer.write(outputStream, object);
			}
		}
	}
}
