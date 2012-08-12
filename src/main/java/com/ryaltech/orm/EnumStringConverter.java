package com.ryaltech.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Converts String fields to and from particular enum
 *
 * @author Alex Rykov
 *
 * @param <E>
 *            particular enum to/from which to convert
 */
public class EnumStringConverter<E extends Enum<E>> implements Converter<E> {


    /**
     * Factory method to create EnumStringConverter
     *
     * @param <E>
     *            enum type inferred from enumType parameter
     * @param enumType
     *            particular enum class
     * @return instance of EnumConverter
     */
    public static <E extends Enum<E>> EnumStringConverter<E> create(Class<E> enumType) {
        return new EnumStringConverter<E>(enumType);
    }


    private Class<E> enumType;

    private EnumStringConverter() {

    }

    private EnumStringConverter(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public Object convertFieldValueToColumn(E fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        return fieldValue.toString();
    }

    @Override
    public E getFieldValueFromResultSet(ResultSet rs, String columnLabel) throws SQLException {
        String s = rs.getString(columnLabel);
        return s == null ? null : Enum.valueOf(enumType, s);
    }

}
