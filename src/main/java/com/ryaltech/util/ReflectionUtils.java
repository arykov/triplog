package com.ryaltech.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.NumberUtils;

/**
 * Collection of utilities to simplify working with java reflection
 *
 * @author Alex Rykov
 *
 */
public class ReflectionUtils {

    private static Map<Class<?>, Class<?>> boxTypes = new HashMap<Class<?>, Class<?>>();
    private static Map<Class<?>, Set<Class<?>>> promotions = new HashMap<Class<?>, Set<Class<?>>>();

    static {

        boxTypes.put(boolean.class, Boolean.class);
        boxTypes.put(byte.class, Byte.class);
        boxTypes.put(char.class, Character.class);
        boxTypes.put(short.class, Short.class);
        boxTypes.put(int.class, Integer.class);
        boxTypes.put(long.class, Long.class);
        boxTypes.put(float.class, Float.class);
        boxTypes.put(double.class, Double.class);

        addPromotion(Byte.class, Short.class);
        addPromotion(Byte.class, Integer.class);
        addPromotion(Byte.class, Long.class);
        addPromotion(Byte.class, Float.class);
        addPromotion(Byte.class, Double.class);

        addPromotion(Character.class, Integer.class);
        addPromotion(Character.class, Long.class);
        addPromotion(Character.class, Float.class);
        addPromotion(Character.class, Double.class);

        addPromotion(Short.class, Integer.class);
        addPromotion(Short.class, Long.class);
        addPromotion(Short.class, Float.class);
        addPromotion(Short.class, Double.class);

        addPromotion(Integer.class, Long.class);
        addPromotion(Integer.class, Float.class);
        addPromotion(Integer.class, Double.class);

        addPromotion(Long.class, Float.class);
        addPromotion(Long.class, Double.class);

        addPromotion(Float.class, Double.class);

    }

    private static void addPromotion(Class<?> from, Class<?> to) {
        Set<Class<?>> set = promotions.get(from);
        if (set == null) {
            set = new HashSet<Class<?>>();
            promotions.put(from, set);
        }
        set.add(to);
    }

    /**
     * Asserts the given array of method arguments will work when invoking the
     * given method. If not, a RuntimeException will be thrown with a detailed
     * description of the problem. If you went ahead an invoked the method,
     * you'd also get an exception but with much less detail about the problem.
     *
     * @param m
     *            The method you want to invoke.
     * @param args
     *            The list of arguments you plan on sending to the method.
     */
    public static void assertMethodArgs(Method m, Object... args) {

        String message = null;

        Class<?>[] argTypes = m.getParameterTypes();

        if (argTypes.length != args.length) {
            message = String.format("expected %d args, received %d)",
                    argTypes.length, args.length);
        } else {
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> clazz = argTypes[i];
                Object arg = args[i];
                if (arg == null) {
                    if (clazz.isPrimitive()) {
                        message = String.format("arg %d is a primitive, must not be null", i);
                        break;
                    }
                } else if (!isPromotableFrom(clazz, arg.getClass())) {
                    message = String.format("arg %d expected type %s, got type %s", i, clazz, arg.getClass());
                    break;
                }
            }
        }

        if (message != null) {
            throw new RuntimeException(
                    String.format("Error invoking %s with arguments (%s): %s",
                            m, StringUtils.join(", ", args), message));
        }
    }

    private static Object convertIfNecessary(Object value, Class<?> requiredType) throws IllegalArgumentException {
        if (value == null || isPromotableFrom(requiredType, value.getClass())) {
            return value;
        }
        
        if (value instanceof Number) {
            if (requiredType.isPrimitive()) {
                requiredType = boxTypes.get(requiredType);
            }
            return NumberUtils.convertNumberToTargetClass((Number) value, (Class<Number>)requiredType);
        }
        throw new IllegalArgumentException(String.format("Cannot convert %s to type %s", value, requiredType));
    }

    /**
     * This method will find a field with particular name in the clazz
     * hierarchy. If multiple fields with the same name exist in hierarchy, the
     * field in the class closest to the clazz gets returned.
     *
     * @param clazz
     *            class to look for fields in the hierarchy
     * @param fieldName
     *            fieldName to find in the class
     * @return found field or nul if no fields are found
     */
    public static Field getDeclaredFieldInHierarchy(Class<?> clazz, String fieldName) {
        assert fieldName != null : "fieldName cannot be null";
        Field[] fields = getDeclaredFieldsInHierarchy(clazz);
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns an array of all declared fields in the given class and all
     * super-classes.
     */
    public static Field[] getDeclaredFieldsInHierarchy(Class<?> clazz) {

        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("Primitive types not supported.");
        }

        List<Field> result = new ArrayList<Field>();

        while (true) {

            if (clazz == Object.class) {
                break;
            }

            result.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        return result.toArray(new Field[result.size()]);
    }

    /**
     * Gets value of field deepFieldName on object to value. It traverses nested
     * fields with exception of array and Map elements
     *
     * @param object
     * @param deepFieldName
     * @return value of the field
     */
    public static Object getFieldValue(Object object, String deepFieldName) {
        assert deepFieldName != null : "Field name is null";
        assert object != null : "Trying to extract " + deepFieldName;
        int delimPos = deepFieldName.indexOf('.');
        String fieldName, remainder;
        // no . or . first are no good.
        if (delimPos == -1) {
            fieldName = deepFieldName;
            remainder = null;
        } else if (delimPos > 0) {
            fieldName = deepFieldName.substring(0, delimPos);
            int l = deepFieldName.length();
            if (l > delimPos + 1) {
                remainder = deepFieldName.substring(delimPos + 1, l);
            } else {
                throw new RuntimeException(String.format("Illegal field %s in object %s. Likely double \".\" or fieldName ending with \".\"", deepFieldName, object.getClass()));
            }
        } else {
            throw new RuntimeException(String.format("Illegal field %s in object %s. Likely double \".\" or fieldName starts with \".\"", deepFieldName, object.getClass()));
        }

        try {
            Field field = getDeclaredFieldInHierarchy(object.getClass(), fieldName);
            if (field == null) {
                throw new RuntimeException(String.format("Class %s does not have field %s in its hierarchy.", object.getClass(), fieldName));
            }
            field.setAccessible(true);

            Object fieldValue = field.get(object);

            if (remainder != null) {
                return getFieldValue(fieldValue, remainder);
            } else {
                return fieldValue;
            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Invokes method by name and changes its accessibility level if required.
     * So you can use it to invoke private members
     *
     * @param o
     * @param methodName
     * @param setAccessibilityIfNeeded
     * @param args
     * @return
     * @throws IllegalArgumentException
     * @throws RuntimeException
     * @throws MethodNotFoundException
     */
    private static Object invokeByName(Object o, String methodName, boolean setAccessibilityIfNeeded, Object... args) throws IllegalArgumentException, RuntimeException, MethodNotFoundException {

        assert o != null;
        assert methodName != null;

        Method[] methods = setAccessibilityIfNeeded ? o.getClass().getDeclaredMethods() : o.getClass().getMethods();

        for (Method m : methods) {
            if (methodName.equals(m.getName())) {
                try {
                    if (setAccessibilityIfNeeded) {
                        m.setAccessible(true);
                    }
                    return m.invoke(o, args);
                } catch (IllegalArgumentException e) {
                    assertMethodArgs(m, args); // This throws an exception if
                    // there's a problem with the
                    // arguments
                    throw e; // In case there was some other problem
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new WrapperException(e.getCause());
                }
            }
        }

        throw new MethodNotFoundException(
                String.format("Method %s not found in class %s",
                        methodName, o.getClass().getName()));
    }

    /**
     * Invokes method by methodName in object o and passes args to it
     *
     * @param o
     * @param methodName
     * @param args
     * @return whatever method returns
     * @throws IllegalArgumentException
     * @throws RuntimeException
     * @throws MethodNotFoundException
     */
    public static Object invokeByName(Object o, String methodName, Object... args) throws IllegalArgumentException, RuntimeException, MethodNotFoundException {

        return invokeByName(o, methodName, false, args);
    }

    /**
     * For testing only
     *
     * @param o
     * @param methodName
     * @param args
     * @return
     */
    public static Object invokeByNameLowVisibility(Object o, String methodName, Object... args) throws IllegalArgumentException, RuntimeException, MethodNotFoundException {
        return invokeByName(o, methodName, true, args);
    }

    /**
     * Returns true if a variable of a given class can be assigned from a
     * variable of a given class. This is equivalent to the
     * {@link Class#isAssignableFrom(Class)} method, but converts primitive
     * types to their boxed versions, and will consider promotions, e.g. from
     * int to long.
     *
     * @param assigneeClass
     *            Class of the variable to which the value being assigned, for
     *            example the argument in a method call.
     * @param valueClass
     *            Class of value being assigned.
     */
    public static boolean isPromotableFrom(Class<?> assigneeClass, Class<?> valueClass) {

        assert assigneeClass != null;
        assert valueClass != null;

        if (assigneeClass.isPrimitive()) {
            Class<?> boxClass = boxTypes.get(assigneeClass);
            assert boxClass != null : "Can't find box type for " + assigneeClass.getName();
            assigneeClass = boxClass;
        }

        if (valueClass.isPrimitive()) {
            Class<?> boxClass = boxTypes.get(valueClass);
            assert boxClass != null : "Can't find box type for " + valueClass.getName();
            valueClass = boxClass;
        }

        assert assigneeClass != null;
        assert valueClass != null;

        if (assigneeClass.isAssignableFrom(valueClass)) {
            return true;
        } else if (promotions.get(valueClass) != null && promotions.get(valueClass).contains(assigneeClass)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Sets value of field deepFieldName on object to value. It traverses nested
     * fields with exception of array and Map elements
     *
     * @param object
     * @param deepFieldName
     * @param value
     */
    public static void setFieldValue(Object object, String deepFieldName, Object value) {
        assert deepFieldName != null : "Field name is null";
        assert object != null : "Object is null";
        int delimPos = deepFieldName.lastIndexOf('.');
        String fieldName, startPart;
        // no . or . first are no good.
        if (delimPos == -1) {
            fieldName = deepFieldName;
            startPart = null;

        } else if (delimPos > 0) {
            int l = deepFieldName.length();
            fieldName = deepFieldName.substring(delimPos + 1, l);
            if (l > delimPos + 1) {
                startPart = deepFieldName.substring(0, delimPos);
            } else {
                throw new RuntimeException(String.format("Illegal field %s in object %s. Likely double \".\" or fieldName ending with \".\"", deepFieldName, object.getClass()));
            }
            Object immideateFieldContainer = getFieldValue(object, startPart);
            if (immideateFieldContainer == null) {
                throw new RuntimeException(String.format("Field %s in object %s is null.", startPart, object));
            }
            object = immideateFieldContainer;
        } else {
            throw new RuntimeException(String.format("Illegal field %s in object %s. Likely \".\" is the only part of the deepFieldName.", deepFieldName, object.getClass()));
        }

        try {
            Field field = getDeclaredFieldInHierarchy(object.getClass(), fieldName);
            if (field == null) {
                throw new RuntimeException(String.format("Class %s does not have field %s in its hierarchy.", object.getClass(), fieldName));
            }
            field.setAccessible(true);
            value = convertIfNecessary(value, field.getType());
            field.set(object, value);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

    }
    /**
     * 
     * @return method name of from which getMethodName got called
     */
    public static String getMethodName(){
		return new Exception().getStackTrace()[1].getMethodName();
	}

}
