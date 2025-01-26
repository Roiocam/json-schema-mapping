/* (C)2025 */
package com.roiocam.jsm.tools;

import java.lang.reflect.Field;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.roiocam.jsm.schema.Schema;
import com.roiocam.jsm.schema.SchemaNode;
import com.roiocam.jsm.schema.SchemaPath;
import com.roiocam.jsm.schema.SchemaTypeMetadata;
import com.roiocam.jsm.schema.SchemaValue;

/**
 * Maintainer all schema operation, which only working with objects, won't work with json string.
 */
public class SchemaOperator {

    /**
     * Generates a schema and example JSON from a Java object.
     *
     * @param obj the Java object
     * @return a schema.SchemaNode representing the schema
     */
    public static SchemaNode generateSchema(Class<?> obj) {
        return processObject(obj, null);
    }

    /**
     * Evaluate a schema JSON to a {@link SchemaValue} by using @{@link SchemaNode} and {@link SchemaPath}
     * @param schema the schema node
     * @param path               the schema path
     * @param json               the JSON string
     * @return
     */
    public static SchemaValue parseValue(SchemaNode schema, SchemaPath path, String json) {
        ReadContext ctx = JsonPath.parse(json);
        return processSchema(schema, path, ctx);
    }

    /**
     * Evaluate a schema JSON to a Java Object by using @{@link SchemaNode} and {@link SchemaPath}
     * @param schema
     * @param path
     * @param json
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T parseObject(
            SchemaNode schema, SchemaPath path, String json, Class<T> clazz) {
        ReadContext ctx = JsonPath.parse(json);
        return processObject(schema, path, ctx, clazz);
    }

    /**
     * verify the {@link SchemaPath} is match to schema {@link SchemaNode}
     *
     * @param schema
     * @param path
     * @return
     */
    public static boolean schemaMatch(SchemaNode schema, SchemaPath path) {
        Map<String, Schema<Class<?>>> schemaChildren = schema.getChildren();
        Map<String, Schema<String>> pathChildren = path.getChildren();
        if (schemaChildren.isEmpty() && pathChildren.isEmpty()) {
            // neither schema nor path has parent
            return (schema.getParent() == null && path.getParent() == null)
                    || (schema.getParent() != null && path.getParent() != null);
        }

        // check if the children match
        for (Map.Entry<String, Schema<Class<?>>> entry : schemaChildren.entrySet()) {
            if (!pathChildren.containsKey(entry.getKey())) {
                return Boolean.FALSE;
            }
            SchemaNode child = (SchemaNode) entry.getValue();
            SchemaPath childPath = (SchemaPath) pathChildren.get(entry.getKey());
            if (!schemaMatch(child, childPath)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Processes the schema to generate a {@link SchemaValue} object
     * @param schema
     * @param path
     * @param ctx
     * @return
     */
    private static SchemaValue processSchema(SchemaNode schema, SchemaPath path, ReadContext ctx) {
        Map<String, Schema<Class<?>>> schemaChildren = schema.getChildren();
        if (schemaChildren.isEmpty()) {
            return new SchemaValue(ctx.read(path.getValue(), schema.getValue()), path);
        }

        SchemaValue result = new SchemaValue(null, path);
        Map<String, Schema<String>> pathChildren = path.getChildren();
        for (Map.Entry<String, Schema<Class<?>>> entry : schemaChildren.entrySet()) {
            SchemaNode child = (SchemaNode) entry.getValue();
            SchemaPath childPath = (SchemaPath) pathChildren.get(entry.getKey());
            result.addChild(entry.getKey(), processSchema(child, childPath, ctx));
        }
        return result;
    }

    /**
     * Processes the schema to generate a Java object
     * @param schema
     * @param path
     * @param ctx
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> T processObject(
            SchemaNode schema, SchemaPath path, ReadContext ctx, Class<T> clazz) {
        try {
            if (!schema.getValue().equals(clazz)) {
                throw new IllegalArgumentException("Schema value does not match class type");
            }
            // If there are no children (leaf node), just extract the value
            Map<String, Schema<Class<?>>> schemaChildren = schema.getChildren();
            if (clazz.isPrimitive() || schemaChildren.isEmpty()) {
                return ctx.read(path.getValue(), clazz);
            }

            // Create an instance of the class
            T obj = clazz.getConstructor().newInstance();

            // For each child node, recursively parse and set the value
            Map<String, Schema<String>> pathChildren = path.getChildren();
            for (Map.Entry<String, Schema<Class<?>>> entry : schemaChildren.entrySet()) {
                String fieldName = entry.getKey();
                SchemaNode childSchema = (SchemaNode) entry.getValue();
                SchemaPath childPath = (SchemaPath) pathChildren.get(fieldName);

                // Recursively process the child object
                Object childValue =
                        processObject(childSchema, childPath, ctx, childSchema.getValue());
                setFieldValue(obj, fieldName, childValue);
            }

            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing object", e);
        }
    }

    /**
     * Processes the Java object to generate schema and example data.
     *
     * @param clz the object class to process
     * @return a schema.SchemaNode representing the object
     */
    private static SchemaNode processObject(Class<?> clz, SchemaNode parent) {
        SchemaNode current = new SchemaNode(clz, parent);
        try {
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                if (isPrimitiveOrWrapper(fieldType) || fieldType == String.class) {
                    // Primitive or string types
                    current.addChild(field.getName(), new SchemaNode(fieldType, current));
                } else {
                    // Nested object
                    SchemaNode childSchema = processObject(fieldType, current);
                    current.addChild(field.getName(), childSchema);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return current;
    }

    /**
     * Determines if a class is a primitive type or a wrapper type.
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        for (SchemaTypeMetadata value : SchemaTypeMetadata.values()) {
            if (value.getClazz().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the value of a field on the provided object
     *
     * @param obj   the object
     * @param field the field name
     * @param value the value to set
     */
    private static void setFieldValue(Object obj, String field, Object value) {
        try {
            var fieldRef = obj.getClass().getDeclaredField(field);
            fieldRef.setAccessible(true);
            fieldRef.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error setting field value", e);
        }
    }
}
