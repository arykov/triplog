package com.ryaltech.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ryaltech.util.ReflectionUtils;

/**
 * Maps a column in the database to a corresponding field in a Java class.
 *
*
 */
public class Column {

    private String columnName;

    private String fieldName;

    private Converter converter;

    public Column(String name) {
        this(name, name);
    }

    public Column(String fieldName, String columnName) {
        this(fieldName, columnName, DefaultConverter.getInstance());
    }

    public Column(String fieldName, String columnName, Converter converter) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.converter = converter;
    }

    public String getColumnName() {
        return columnName;
    }

    public Converter getConverter() {
        return converter;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the field value for this column from the given object. If this
     * column has a Converter, the value is converted to database format before
     * returning.
     */
    public Object getFieldValueAsColumn(Object object) {
        /*
         * ReflectionUtils.setFieldValue(object, fieldName, fieldValue);
         */

        Object fieldValue = ReflectionUtils.getFieldValue(object, fieldName);
        //Object fieldValue = new DirectTraverseFieldAccessor(object).getPropertyValue(fieldName);
        return converter.convertFieldValueToColumn(fieldValue);
    }

    public Column setConverter(Converter converter) {
        this.converter = converter;
        return this;
    }

    /**
     * Sets the field value in an object, using the value returned from the
     * database. If this column has a converter, the value is converted to field
     * format before it is set.
     */
    public void setFieldValueFromResultSet(Object object, ResultSet rs, String columnLabel) {
        try {
            Object fieldValue = converter.getFieldValueFromResultSet(rs, columnLabel);
            /*
             *
             */
            ReflectionUtils.setFieldValue(object, fieldName, fieldValue);
            //new DirectTraverseFieldAccessor(object).setPropertyValue(fieldName, fieldValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
