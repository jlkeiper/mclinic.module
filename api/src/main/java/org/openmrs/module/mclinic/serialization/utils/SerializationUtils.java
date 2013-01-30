package org.openmrs.module.mclinic.serialization.utils;

import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.OutputStream;
import java.lang.reflect.Method;

public class SerializationUtils {

	/**
	 * Invoke the serialization process
	 *
	 * @param serializerKey    the key to the implementation of the serializer
	 * @param defaultClassName the default name of the serializer
	 * @param outputStream     the output stream where the serializer will write the data
	 * @param data             the data to be serialized
	 * @throws Exception when the serialization process failed
	 */
	public static void invokeSerializationMethod(final String serializerKey, final String defaultClassName,
	                                             final OutputStream outputStream, final Object data) throws Exception {
		String serializerClass = Context.getAdministrationService().getGlobalProperty(serializerKey);

		if (serializerClass == null || serializerClass.length() == 0)
			serializerClass = defaultClassName;

		Object obj = OpenmrsClassLoader.getInstance().loadClass(serializerClass).newInstance();

		Method method = obj.getClass().getMethod("write", new Class[]{OutputStream.class, Object.class});
		method.invoke(obj, outputStream, data);
	}

}
