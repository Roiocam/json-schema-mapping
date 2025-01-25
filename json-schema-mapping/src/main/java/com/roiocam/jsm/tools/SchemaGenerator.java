/* (C)2025 */
package com.roiocam.jsm.tools;

import java.lang.reflect.Field;

import com.roiocam.jsm.schema.SchemaNode;

public class SchemaGenerator {

    /**
     * Generates a schema and example JSON from a Java object.
     *
     * @param obj the Java object
     * @return a schema.SchemaNode representing the schema
     */
    public static SchemaNode generateSchema(Object obj) {
        return processObject(obj, null);
    }

    /**
     * Processes the Java object to generate schema and example data.
     *
     * @param obj the object to process
     * @return a schema.SchemaNode representing the object
     */
    private static SchemaNode processObject(Object obj, SchemaNode parent) {
        SchemaNode current = new SchemaNode(obj.getClass(), parent);
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                Class<?> fieldType = field.getType();

                if (isPrimitiveOrWrapper(fieldType) || fieldType == String.class) {
                    // Primitive or string types
                    current.addChild(field.getName(), new SchemaNode(fieldType, current));
                } else {
                    // Nested object
                    SchemaNode childSchema =
                            processObject(
                                    value != null
                                            ? value
                                            : fieldType.getDeclaredConstructor().newInstance(),
                                    current);
                    current.addChild(field.getName(), childSchema);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return current;
    }

    /**
     * Determines if a class is a primitive type or a wrapper type.
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == Integer.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Long.class
                || clazz == Short.class
                || clazz == Boolean.class
                || clazz == Byte.class
                || clazz == Character.class;
    }
}
