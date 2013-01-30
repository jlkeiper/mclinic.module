package org.openmrs.module.mclinic.api.db.hibernate.type;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.util.OpenmrsUtil;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CohortDefinitionType implements UserType {

	private static final int[] SQL_TYPES = {Types.VARCHAR};

	/**
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	/**
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	@Override
	public Class<CohortDefinition> returnedClass() {
		return CohortDefinition.class;
	}

	/**
	 * @see org.hibernate.usertype.UserType#equals(Object, Object)
	 */
	@Override
	public boolean equals(final Object x, final Object y) throws HibernateException {
		return OpenmrsUtil.nullSafeEquals(x, y);
	}

	/**
	 * @see org.hibernate.usertype.UserType#hashCode(Object)
	 */
	@Override
	public int hashCode(final Object x) throws HibernateException {
		return x.hashCode();
	}

	/**
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, String[], Object)
	 */
	@Override
	public Object nullSafeGet(final ResultSet resultSet, final String[] names, final Object owner) throws HibernateException, SQLException {
		String uuid = resultSet.getString(names[0]);
		if (StringUtils.isBlank(uuid))
			return null;
		return Context.getService(CohortDefinitionService.class).getDefinitionByUuid(uuid);
	}

	/**
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, Object, int)
	 */
	@Override
	public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int index) throws HibernateException, SQLException {
		CohortDefinition definition = (CohortDefinition) value;
		String uuid = (definition == null ? null : definition.getUuid());
		preparedStatement.setString(index, uuid);
	}

	/**
	 * @see org.hibernate.usertype.UserType#deepCopy(Object)
	 */
	@Override
	public Object deepCopy(final Object value) throws HibernateException {
		return value;
	}

	/**
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	@Override
	public boolean isMutable() {
		return Boolean.TRUE;
	}

	/**
	 * @see org.hibernate.usertype.UserType#disassemble(Object)
	 */
	@Override
	public Serializable disassemble(final Object value) throws HibernateException {
		if (value == null)
			return null;
		return ((CohortDefinition) value).getUuid();
	}

	/**
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, Object)
	 */
	@Override
	public Object assemble(final Serializable serializable, final Object owner) throws HibernateException {
		String uuid = (String) serializable;
		return Context.getService(DefinitionService.class).getDefinitionByUuid(uuid);
	}

	/**
	 * @see org.hibernate.usertype.UserType#replace(Object, Object, Object)
	 */
	@Override
	public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
		return original;
	}
}
