package com.ryaltech.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Converter that takes objects as is and does not perform any conversions beyond JDBC
 *
 * @author Alex Rykov
 *
 */
public class DefaultConverter implements Converter{

    private static final DefaultConverter INSTANCE = new DefaultConverter();

    public static DefaultConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public Object convertFieldValueToColumn(Object fieldValue) {
        return fieldValue;
    }

    @Override
    public Object getFieldValueFromResultSet(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getObject(columnLabel);
    }

}
