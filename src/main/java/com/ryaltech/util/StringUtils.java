package com.ryaltech.util;

import java.util.Arrays;
import java.util.List;

public final class StringUtils {

    /**
     * Null-safe equals method. Returns true if both strings are null, or if
     * neither are null and comparing them with String.equals returns true.
     */
    public static boolean equals(String s1, String s2) {
        return ObjectUtils.equals(s1, s2);
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    /**
     * Returns a string containing the string representation of each item in the
     * given list, separated by the given separator.
     */
    public static String join(String separator, List<?> items) {

    	if(separator == null)throw new AssertionError("separator cannot be null");
        if(items == null)throw new AssertionError("items cannot be null");


        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : items) {

            if (!first) {
                sb.append(separator);
            }

            sb.append(item);

            first = false;
        }

        return sb.toString();
    }

    /**
     * Returns a string containing the string representation of each item in the
     * given list, separated by the given separator.
     */
    public static String join(String separator, Object... items) {

    	if(separator == null)throw new AssertionError("separator cannot be null");
        if(items == null)throw new AssertionError("items cannot be null");

        return join(separator, Arrays.asList(items));
    }

    /**
     * Array-aware toString method. If the given value is an array, passes it to
     * one of the toString classes in the JDK's Arrays class, else returns the
     * result of its toString method.
     */
    public static String toString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof boolean[]) {
            return Arrays.toString((boolean[]) value);
        } else if (value instanceof char[]) {
            return Arrays.toString((char[]) value);
        } else if (value instanceof byte[]) {
            return Arrays.toString((byte[]) value);
        } else if (value instanceof short[]) {
            return Arrays.toString((short[]) value);
        } else if (value instanceof int[]) {
            return Arrays.toString((int[]) value);
        } else if (value instanceof long[]) {
            return Arrays.toString((long[]) value);
        } else if (value instanceof float[]) {
            return Arrays.toString((float[]) value);
        } else if (value instanceof double[]) {
            return Arrays.toString((double[]) value);
        } else if (value instanceof Object[]) {
            return Arrays.toString((Object[]) value);
        } else {
            return value.toString();
        }
    }

    private StringUtils() {
    }
}
