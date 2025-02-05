/* (C)2025 */
package com.roiocam.jsm.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.api.ISchemaPath;
import com.roiocam.jsm.api.ISchemaValue;
import com.roiocam.jsm.facade.JSONFactory;
import com.roiocam.jsm.facade.JSONPathContext;
import com.roiocam.jsm.schema.SchemaTypeMetadata;
import com.roiocam.jsm.schema.array.ArraySchemaNode;
import com.roiocam.jsm.schema.array.ArraySchemaPath;
import com.roiocam.jsm.schema.array.ArraySchemaValue;
import com.roiocam.jsm.schema.map.MapSchemaNode;
import com.roiocam.jsm.schema.obj.ObjSchemaNode;
import com.roiocam.jsm.schema.obj.ObjSchemaPath;
import com.roiocam.jsm.schema.obj.ObjSchemaValue;
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
            return getArraySchemaNode(clz, boxedClz, parent);
        }

        // Object Node
        try {
            ObjSchemaNode current = new ObjSchemaNode(clz, parent);
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
                                getArraySchemaNode(
                                        fieldType, fieldType.getComponentType(), current);
                        current.addChild(field.getName(), arraySchemaNode);
                        continue;
                    }
                    // Map Type
                    if (metadata == SchemaTypeMetadata.MAP) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericType;
                            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                            if (actualTypeArguments.length == 2) {
                                Class<?> keyElementClass = (Class<?>) actualTypeArguments[0];
                                Class<?> valueElementClass = (Class<?>) actualTypeArguments[1];
                                ISchemaNode keyNode = processObject(keyElementClass, null);
                                ISchemaNode valueNode = processObject(valueElementClass, null);
                                MapSchemaNode mapSchemaNode =
                                        new MapSchemaNode(clz, parent, keyNode, valueNode);
                                keyNode.setParent(mapSchemaNode);
                                valueNode.setParent(mapSchemaNode);
                                current.addChild(field.getName(), mapSchemaNode);
                            }
                        }
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
                                    getArraySchemaNode(fieldType, listElementClass, current);
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

    private static ArraySchemaNode getArraySchemaNode(
            Class<?> clz, Class<?> boxClz, ISchemaNode parent) {
        ISchemaNode schemaNode = processObject(boxClz, null);
        ArraySchemaNode arraySchemaNode = new ArraySchemaNode(clz, schemaNode, parent);
        schemaNode.setParent(arraySchemaNode);
        return arraySchemaNode;
    }

    /**
     * Evaluate a schema JSON to a {@link SchemaValue} by using @{@link SchemaNode} and {@link SchemaPath}
     *
     * @param schema the schema node
     * @param path   the schema path
     * @param json   the JSON string
     * @return
     */
    public static ISchemaValue evaluateValue(
            ISchemaNode schema, ISchemaPath path, JSONFactory factory, String json) {
        JSONPathContext ctx = factory.createPathContext(json);
        return evaluateValue(schema, path, ctx, null);
    }

    private static ISchemaValue evaluateValue(
            ISchemaNode schema, ISchemaPath path, JSONPathContext ctx, ISchemaValue parent) {
        if (!schemaMatch(schema, path)) {
            throw new IllegalArgumentException("Schema and path do not match");
        }
        // 1. processing array schema
        if (schema instanceof ArraySchemaNode) {
            ArraySchemaValue schemaValue = new ArraySchemaValue(parent);
            ArraySchemaNode arraySchema = (ArraySchemaNode) schema;
            ArraySchemaPath arrayPath = (ArraySchemaPath) path;
            ISchemaNode eleNode = arraySchema.getParamType();
            ISchemaPath elePath = arrayPath.getValue().get(0);
            evaluateValue(eleNode, elePath, ctx, schemaValue);
            return schemaValue;
        }
        // 2. processing object schema
        if (schema instanceof ObjSchemaNode) {
            ObjSchemaNode objSchema = (ObjSchemaNode) schema;
            ObjSchemaPath objPath = (ObjSchemaPath) path;

            Map<String, ISchemaNode> schemaChildren = objSchema.getChildren();
            Map<String, ISchemaPath> pathChildren = objPath.getChildren();

            if (parent instanceof ArraySchemaValue) {
                ArraySchemaValue parentSchema = (ArraySchemaValue) parent;
                // first get all data group by field name
                Map<String, ArraySchemaValue> mapping = new HashMap<>();
                for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
                    ArraySchemaValue child = new ArraySchemaValue(null);
                    String fieldName = entry.getKey();
                    ISchemaNode childSchema = entry.getValue();
                    ISchemaPath childPath = pathChildren.get(fieldName);
                    // Recursively process the child object
                    evaluateValue(childSchema, childPath, ctx, child);
                    mapping.put(fieldName, child);
                }
                // then merge all data
                for (int i = 0;
                        i
                                < mapping.values().stream()
                                        .mapToInt(e -> e.getValue().size())
                                        .max()
                                        .orElse(0);
                        i++) {
                    ObjSchemaValue<ISchemaValue> schemaValue = new ObjSchemaValue<>(parent);
                    for (Map.Entry<String, ArraySchemaValue> entry : mapping.entrySet()) {
                        ArraySchemaValue child = entry.getValue();
                        if (child.getValue().size() > i) {
                            schemaValue.addChild(entry.getKey(), child.getValue().get(i));
                        }
                    }
                    parentSchema.addElement(schemaValue);
                }
                return parentSchema;
            } else {
                // For each child node, recursively parse and set the value
                ObjSchemaValue<ISchemaValue> schemaValue = new ObjSchemaValue<>(parent);
                for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
                    String fieldName = entry.getKey();
                    ISchemaNode childSchema = entry.getValue();
                    ISchemaPath childPath = (ISchemaPath) pathChildren.get(fieldName);
                    // Recursively process the child object
                    ISchemaValue childValue =
                            evaluateValue(childSchema, childPath, ctx, schemaValue);
                    schemaValue.addChild(fieldName, childValue);
                }
                return schemaValue;
            }
        }
        // 3. processing value schema
        if (schema instanceof SchemaNode) {
            Class<?> value = ((SchemaNode) schema).getValue();
            String pathEval = ((SchemaPath) path).getValue();
            if (parent instanceof ArraySchemaValue) {
                ArraySchemaValue parentSchema = (ArraySchemaValue) parent;
                Collection<?> values = ctx.readArray(pathEval, Collection.class, value);
                for (Object o : values) {
                    parentSchema.addElement(new SchemaValue<>(o, parentSchema));
                }
                return parentSchema;
            } else {
                return new SchemaValue<>(ctx.read(pathEval, value), parent);
            }
        }

        throw new UnsupportedOperationException("Unsupported schema type");
    }

    /**
     * Evaluate a schema JSON to a Java Object by using @{@link SchemaNode} and {@link SchemaPath}
     *
     * @param schema
     * @param path
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T evaluateObject(
            ISchemaNode schema,
            ISchemaPath path,
            JSONFactory factory,
            String json,
            Class<T> clazz) {
        try {
            JSONPathContext ctx = factory.createPathContext(json);
            return evaluateObject(schema, path, ctx, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating object", e);
        }
    }

    /**
     * Processes the schema to generate a Java object.
     *
     * @param schema The schema definition.
     * @param path The path to the JSON data.
     * @param ctx The JSONPath context for evaluating JSON data.
     * @param clazz The target class type to instantiate.
     * @param <T> The generic type of the target object.
     * @return The constructed Java object.
     */
    private static <T> T evaluateObject(
            ISchemaNode schema, ISchemaPath path, JSONPathContext ctx, Class<T> clazz)
            throws NoSuchMethodException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException,
                    NoSuchFieldException {

        if (!schemaMatch(schema, path)) {
            throw new IllegalArgumentException("Schema and path do not match");
        }

        // 1. Processing ArraySchemaNode
        if (schema instanceof ArraySchemaNode) {
            if (!clazz.isArray() && !Collection.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Class type does not support array schema");
            }

            ArraySchemaNode arraySchema = (ArraySchemaNode) schema;
            ArraySchemaPath arrayPath = (ArraySchemaPath) path;
            Class<?> arrayType = arraySchema.getValue();
            ISchemaNode eleNode = arraySchema.getParamType();
            ISchemaPath elePath = arrayPath.getValue().get(0);

            if (clazz.isArray()) {
                // Process each element in the array
                Collection<?> collection = evaluateObject(eleNode, elePath, ctx, Collection.class);

                // Convert the collection to an array if clazz is an array type
                Object array = Array.newInstance(clazz.getComponentType(), collection.size());
                int index = 0;
                for (Object item : collection) {
                    Array.set(array, index++, item);
                }
                return (T) array;
            } else {
                // Process each element in the array
                Object collection = evaluateObject(eleNode, elePath, ctx, arrayType);
                return (T) collection;
            }
        }

        // 2. Processing ObjSchemaNode
        if (schema instanceof ObjSchemaNode) {
            ObjSchemaNode objSchema = (ObjSchemaNode) schema;
            ObjSchemaPath objPath = (ObjSchemaPath) path;

            Map<String, ISchemaNode> schemaChildren = objSchema.getChildren();
            Map<String, ISchemaPath> pathChildren = objPath.getChildren();

            if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
                // first get all data group by field name
                Map<String, Collection<?>> mapping = new HashMap<>();
                for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
                    String fieldName = entry.getKey();
                    ISchemaNode childSchema = entry.getValue();
                    ISchemaPath childPath = pathChildren.get(fieldName);
                    // Recursively process the child object
                    Collection<?> collection =
                            evaluateObject(childSchema, childPath, ctx, Collection.class);
                    mapping.put(fieldName, collection);
                }
                // then merge all data
                List<Object> result = new ArrayList<>();
                Class<?> parentClz = objSchema.getValue();
                for (int i = 0;
                        i < mapping.values().stream().mapToInt(Collection::size).max().orElse(0);
                        i++) {

                    Object obj = parentClz.getConstructor().newInstance();
                    for (Map.Entry<String, Collection<?>> entry : mapping.entrySet()) {
                        Collection<?> child = entry.getValue();
                        Object[] childArray = child.toArray();
                        if (child.size() > i) {
                            Field field = parentClz.getDeclaredField(entry.getKey());
                            field.setAccessible(true);
                            field.set(obj, childArray[i]);
                        }
                    }
                    result.add(obj);
                }
                return (T) result;

            } else {
                // Create an instance of the class
                T obj = clazz.getConstructor().newInstance();

                // For each child node, recursively parse and set the value
                for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
                    String fieldName = entry.getKey();
                    ISchemaNode childSchema = entry.getValue();
                    ISchemaPath childPath = pathChildren.get(fieldName);

                    // Determine the field's type via reflection
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    // Recursively process the child object
                    Object childValue = evaluateObject(childSchema, childPath, ctx, fieldType);

                    // Set the field value
                    field.set(obj, childValue);
                }

                return obj;
            }
        }

        // 3. Processing SchemaNode (value schema)
        if (schema instanceof SchemaNode) {
            SchemaNode valueSchema = (SchemaNode) schema;
            String pathEval = ((SchemaPath) path).getValue();

            // Read the value from the JSON path context
            if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
                // Read an array of values
                T valueList = ctx.readArray(pathEval, clazz, (Class<?>) valueSchema.getValue());
                return valueList;
            } else {
                return (T) ctx.read(pathEval, valueSchema.getValue());
            }
        }

        throw new UnsupportedOperationException("Unsupported schema type");
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
            ISchemaNode schemaNode = ((ArraySchemaNode) schema).getParamType();
            List<ISchemaPath> value = ((ArraySchemaPath) path).getValue();
            // array must be one element
            if (value.size() != 1) {
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
            Map<String, ISchemaPath> pathChildren = objPath.getChildren();
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
                ISchemaPath childPath = pathChildren.get(entry.getKey());
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
