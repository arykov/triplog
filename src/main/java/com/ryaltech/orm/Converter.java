package com.ryaltech.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface that defines conversions of T to and from a field in ResultSet
 *
 * @param <T> type of parameter to be converted
 */



public interface Converter<T> {

    public Object convertFieldValueToColumn(T fieldValue);

    public Object getFieldValueFromResultSet(ResultSet rs, String columnLabel) throws SQLException;

}
