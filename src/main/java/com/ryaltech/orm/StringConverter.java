package com.ryaltech.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Converts to a string for database storage. Forces the use of
 * {@link ResultSet#getString(String)} instead of the default
 * {@link ResultSet#getObject(String)}. This is mostly useful for CLOB columns
 * in Oracle, which return a CLOB object rather than a string when getObject()
 * is called.
 *
*
 *
 */
public class StringConverter implements Converter<Object> {

    public StringConverter() {
    }

    @Override
    public Object convertFieldValueToColumn(Object fieldValue) {
        return fieldValue == null ? null : fieldValue.toString();
    }

    @Override
    public Object getFieldValueFromResultSet(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getString(columnLabel);
    }

}
