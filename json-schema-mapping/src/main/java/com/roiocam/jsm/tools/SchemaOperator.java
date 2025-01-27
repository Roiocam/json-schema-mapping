/* (C)2025 */
package com.roiocam.jsm.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.api.ISchemaPath;
import com.roiocam.jsm.api.ISchemaValue;
import com.roiocam.jsm.schema.SchemaTypeMetadata;
import com.roiocam.jsm.schema.array.ArraySchemaNode;
import com.roiocam.jsm.schema.array.ArraySchemaPath;
import com.roiocam.jsm.schema.obj.ObjSchemaNode;
import com.roiocam.jsm.schema.obj.ObjSchemaPath;
import com.roiocam.jsm.schema.value.SchemaNode;
import com.roiocam.jsm.schema.value.SchemaPath;
import com.roiocam.jsm.schema.value.SchemaValue;

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
    public static ISchemaNode generateSchema(Class<?> obj) {
        return processObject(obj, null);
    }

    /**
     * Processes the Java object to generate schema and example data.
     *
     * @param clz the object class to process
     * @return a schema.SchemaNode representing the object
     */
    private static ISchemaNode processObject(Class<?> clz, ISchemaNode parent) {
        // Root Node
        if (clz == null) {
            return new SchemaNode(null, parent);
        }
        SchemaTypeMetadata typeMetadata = SchemaTypeMetadata.fromClass(clz);
        Class<?> boxedClz = clz;
        if (typeMetadata != null) {
            boxedClz = typeMetadata.getClazz();
        }
        // Leaf Node
        if (typeMetadata != null && !typeMetadata.isCollection()) {
            return new SchemaNode(boxedClz, parent);
        }

        // Array Node
        if (typeMetadata != null && typeMetadata.isCollection()) {
            return getArraySchemaNode(boxedClz, parent);
        }

        // Object Node
        try {
            ObjSchemaNode current = new ObjSchemaNode(parent);
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                SchemaTypeMetadata metadata = SchemaTypeMetadata.fromClass(fieldType);
                // Nested object
                if (metadata == null) {
                    current.addChild(field.getName(), processObject(fieldType, current));
                    continue;
                }
                // Plain value
                if (!metadata.isCollection()) {
                    ISchemaNode childNode = processObject(fieldType, current);
                    current.addChild(field.getName(), childNode);
                    continue;
                }
                // Collection type
                if (metadata.isCollection()) {
                    // Array type
                    if (fieldType.isArray()) {
                        ArraySchemaNode arraySchemaNode =
                                getArraySchemaNode(fieldType.getComponentType(), current);
                        current.addChild(field.getName(), arraySchemaNode);
                        continue;
                    }
                    // Collection type
                    if (field.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType parameterizedType =
                                (ParameterizedType) field.getGenericType();
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (actualTypeArguments.length > 0
                                && actualTypeArguments[0] instanceof Class) {
                            Class<?> listElementClass = (Class<?>) actualTypeArguments[0];
                            ArraySchemaNode arraySchemaNode =
                                    getArraySchemaNode(listElementClass, current);
                            current.addChild(field.getName(), arraySchemaNode);
                        }
                        continue;
                    }
                }
                throw new UnsupportedOperationException("Unsupported type: " + metadata);
            }
            return current;
        } catch (Exception e) {
            throw new RuntimeException("Error processing object", e);
        }
    }

    private static ArraySchemaNode getArraySchemaNode(Class<?> clz, ISchemaNode parent) {
        ISchemaNode schemaNode = processObject(clz, null);
        ArraySchemaNode arraySchemaNode = new ArraySchemaNode((ISchemaNode) schemaNode, parent);
        schemaNode.setParent(arraySchemaNode);
        return arraySchemaNode;
    }

    /**
     * Evaluate a schema JSON to a {@link SchemaValue} by using @{@link SchemaNode} and {@link SchemaPath}
     * @param schema the schema node
     * @param path               the schema path
     * @param json               the JSON string
     * @return
     */
    public static ISchemaValue evaluateValue(ISchemaNode schema, ISchemaPath path, String json) {
        try {
            ReadContext ctx = JsonPath.parse(json);
            return evaluateValue(schema, path, ctx);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating object", e);
        }
    }

    private static ISchemaValue evaluateValue(ISchemaNode schema, ISchemaPath path, ReadContext ctx)
            throws NoSuchMethodException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        // TODO
        return null;
        //        if (!schemaMatch(schema, path)) {
        //            throw new IllegalArgumentException("Schema and path do not match");
        //        }
        //        //
        //        if (schema instanceof ArraySchemaNode) {
        //            // TODO
        //
        //        }
        //        if (schema instanceof ObjSchemaNode) {
        //            Class<?> value = ((SchemaNode) schema).getValue();
        //            if (!value.equals(clazz)) {
        //                throw new IllegalArgumentException("Schema value does not match class
        // type");
        //            }
        //            // Create an instance of the class
        //            T obj = clazz.getConstructor().newInstance();
        //            ObjSchemaNode objSchema = (ObjSchemaNode) schema;
        //            ObjSchemaPath objPath = (ObjSchemaPath) path;
        //
        //            Map<String, ISchemaNode> schemaChildren = objSchema.getChildren();
        //            Map<String, ISchemaValue> pathChildren = objPath.getChildren();
        //
        //            // For each child node, recursively parse and set the value
        //            for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
        //                String fieldName = entry.getKey();
        //                ISchemaNode childSchema = entry.getValue();
        //                ISchemaPath childPath = (ISchemaPath) pathChildren.get(fieldName);
        //                // Recursively process the child object
        //                Object childValue =
        //                        processObject(childSchema, childPath, ctx,
        // childSchema.getValue());
        //                setFieldValue(obj, fieldName, childValue);
        //            }
        //
        //            return obj;
        //        }
        //
        //        if (schema instanceof SchemaNode) {
        //            Class<?> value = ((SchemaNode) schema).getValue();
        //            if (!value.equals(clazz)) {
        //                throw new IllegalArgumentException("Schema value does not match class
        // type");
        //            }
        //            String pathEval = ((SchemaPath) path).getValue();
        //            return ctx.read(pathEval, clazz);
        //        }
        //
        //        throw new UnsupportedOperationException("Unsupported schema type");
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
    public static <T> T evaluateObject(
            ISchemaNode schema, ISchemaPath path, String json, Class<T> clazz) {
        try {
            ReadContext ctx = JsonPath.parse(json);
            return evaluateObject(schema, path, ctx, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating object", e);
        }
    }

    /**
     * verify the {@link SchemaPath} is match to schema {@link SchemaNode}
     *
     * @param schema
     * @param path
     * @return
     */
    public static boolean schemaMatch(ISchemaNode schema, ISchemaPath path) {
        // 1. check if the array schema and array path are the same.
        if (schema instanceof ArraySchemaNode) {
            if (!(path instanceof ArraySchemaPath)) {
                return Boolean.FALSE;
            }
            ISchemaNode schemaNode = ((ArraySchemaNode) schema).getValue();
            List<ISchemaPath> value = ((ArraySchemaPath) path).getValue();
            // Array 只可以取单个值
            if (value.size() != 0) {
                return Boolean.FALSE;
            }
            return schemaMatch(schemaNode, value.get(0));
        }
        // 2. check if the obj schema and obj path are the same.
        if (schema instanceof ObjSchemaNode) {
            if (!(path instanceof ObjSchemaPath)) {
                return Boolean.FALSE;
            }
            ObjSchemaNode objSchema = (ObjSchemaNode) schema;
            ObjSchemaPath objPath = (ObjSchemaPath) path;
            Map<String, ISchemaNode> schemaChildren = objSchema.getChildren();
            Map<String, ISchemaValue> pathChildren = objPath.getChildren();
            if (schemaChildren.isEmpty() && pathChildren.isEmpty()) {
                // neither schema nor path has parent
                return ((objSchema).getParent() == null && (objPath).getParent() == null)
                        || ((objSchema).getParent() != null && (objPath).getParent() != null);
            }
            // check if the children match
            for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
                if (!pathChildren.containsKey(entry.getKey())) {
                    return Boolean.FALSE;
                }
                ISchemaNode child = entry.getValue();
                ISchemaPath childPath = (ISchemaPath) pathChildren.get(entry.getKey());
                if (!schemaMatch(child, childPath)) {
                    return Boolean.FALSE;
                }
            }
        }

        // 3. check if the value schema and value path are the same.
        if (schema instanceof SchemaNode) {
            if (path == null || !(path instanceof SchemaPath)) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
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
    private static <T> T evaluateObject(
            ISchemaNode schema, ISchemaPath path, ReadContext ctx, Class<T> clazz)
            throws NoSuchMethodException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        if (!schemaMatch(schema, path)) {
            throw new IllegalArgumentException("Schema and path do not match");
        }
        //
        if (schema instanceof ArraySchemaNode) {
            // TODO

        }
        if (schema instanceof ObjSchemaNode) {
            Class<?> value = ((SchemaNode) schema).getValue();
            if (!value.equals(clazz)) {
                throw new IllegalArgumentException("Schema value does not match class type");
            }
            // Create an instance of the class
            T obj = clazz.getConstructor().newInstance();
            ObjSchemaNode objSchema = (ObjSchemaNode) schema;
            ObjSchemaPath objPath = (ObjSchemaPath) path;

            Map<String, ISchemaNode> schemaChildren = objSchema.getChildren();
            Map<String, ISchemaValue> pathChildren = objPath.getChildren();

            // For each child node, recursively parse and set the value
            for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
                String fieldName = entry.getKey();
                ISchemaNode childSchema = entry.getValue();
                ISchemaPath childPath = (ISchemaPath) pathChildren.get(fieldName);
                // Recursively process the child object
                // TODO
                //                Object childValue =
                //                        processObject(childSchema, childPath, ctx,
                // childSchema.getValue());
                Object childValue = null;
                setFieldValue(obj, fieldName, childValue);
            }

            return obj;
        }

        if (schema instanceof SchemaNode) {
            Class<?> value = ((SchemaNode) schema).getValue();
            if (!value.equals(clazz)) {
                throw new IllegalArgumentException("Schema value does not match class type");
            }
            String pathEval = ((SchemaPath) path).getValue();
            return ctx.read(pathEval, clazz);
        }

        throw new UnsupportedOperationException("Unsupported schema type");
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
