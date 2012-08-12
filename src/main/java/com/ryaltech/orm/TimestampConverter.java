package com.ryaltech.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Converts java.util.Date and java.sql.Timestamp to java.sql.Timestamp
 *
 * @author Alex Rykov
 *
 */
public class TimestampConverter implements Converter<Date> {

    @Override
    public Object convertFieldValueToColumn(Date fieldValue) {
        if (fieldValue == null || fieldValue instanceof Timestamp) {
            return fieldValue;
        } else if (fieldValue instanceof Date) {
            return new Timestamp((fieldValue).getTime());
        } else {
            throw new RuntimeException("Unsupported class: " + fieldValue.getClass());
        }
    }

    @Override
    public Date getFieldValueFromResultSet(ResultSet rs, String columnLabel) throws SQLException {
        Date date = rs.getTimestamp(columnLabel);
        if(date == null)return null;
        return new Date(date.getTime());
    }

}
