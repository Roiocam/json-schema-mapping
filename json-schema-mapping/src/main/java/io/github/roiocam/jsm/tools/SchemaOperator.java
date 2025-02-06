/* (C)2025 */
package io.github.roiocam.jsm.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.reflect.ClassFields;
import io.github.roiocam.jsm.reflect.ReflectUtils;
import io.github.roiocam.jsm.schema.SchemaTypeMetadata;
import io.github.roiocam.jsm.schema.array.ArraySchemaNode;
import io.github.roiocam.jsm.schema.array.ArraySchemaPath;
import io.github.roiocam.jsm.schema.array.ArraySchemaValue;
import io.github.roiocam.jsm.schema.obj.ObjSchemaNode;
import io.github.roiocam.jsm.schema.obj.ObjSchemaPath;
import io.github.roiocam.jsm.schema.obj.ObjSchemaValue;
import io.github.roiocam.jsm.schema.value.SchemaNode;
import io.github.roiocam.jsm.schema.value.SchemaPath;
import io.github.roiocam.jsm.schema.value.SchemaValue;

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
            ClassFields classFields = ReflectUtils.getClassFields(clz);
            for (String fieldName : classFields.getFieldNames()) {
                Field field = classFields.getField(fieldName);
                Class<?> fieldType = classFields.getFieldType(fieldName);
                SchemaTypeMetadata metadata = SchemaTypeMetadata.fromClass(fieldType);
                // Nested object
                if (metadata == null) {
                    current.addChild(fieldName, processObject(fieldType, current));
                    continue;
                }
                // Plain value
                if (!metadata.isCollection()) {
                    ISchemaNode childNode = processObject(fieldType, current);
                    current.addChild(fieldName, childNode);
                    continue;
                }
                // Collection type
                if (metadata.isCollection()) {
                    // Array type
                    if (fieldType.isArray()) {
                        ArraySchemaNode arraySchemaNode =
                                getArraySchemaNode(
                                        fieldType, fieldType.getComponentType(), current);
                        current.addChild(fieldName, arraySchemaNode);
                        continue;
                    }
                    // Collection type
                    if (field.getGenericType() instanceof ParameterizedType) {
                        Class<?> parameterizedType = classFields.getParameterizedType(fieldName);
                        ArraySchemaNode arraySchemaNode =
                                getArraySchemaNode(fieldType, parameterizedType, current);
                        current.addChild(fieldName, arraySchemaNode);
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
            if (arrayPath.getValue().isEmpty()) {
                return schemaValue;
            }
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
                int length =
                        mapping.values().stream()
                                .mapToInt(e -> e.getValue().size())
                                .max()
                                .orElse(-1);
                // then merge all data
                for (int i = 0; i < length; i++) {
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
     * @param path   The path to the JSON data.
     * @param ctx    The JSONPath context for evaluating JSON data.
     * @param clazz  The target class type to instantiate.
     * @param <T>    The generic type of the target object.
     * @return The constructed Java object.
     */
    private static <T> T evaluateObject(
            ISchemaNode schema, ISchemaPath path, JSONPathContext ctx, Class<T> clazz)
            throws NoSuchMethodException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {

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

            if (clazz.isArray()) {
                if (arrayPath.getValue().isEmpty()) {
                    return (T) Array.newInstance(clazz.getComponentType(), 0);
                }
                ISchemaPath elePath = arrayPath.getValue().get(0);
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
                if (arrayPath.getValue().isEmpty()) {
                    if (Set.class.isAssignableFrom(clazz)) {
                        return (T) Collections.emptySet();
                    }
                    return (T) Collections.emptyList();
                }
                ISchemaPath elePath = arrayPath.getValue().get(0);
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
                int length = mapping.values().stream().mapToInt(Collection::size).max().orElse(-1);
                for (int i = 0; i < length; i++) {
                    Object obj = parentClz.getConstructor().newInstance();
                    ClassFields classFields = ReflectUtils.getClassFields(parentClz);
                    for (Map.Entry<String, Collection<?>> entry : mapping.entrySet()) {
                        Collection<?> child = entry.getValue();
                        Object[] childArray = child.toArray();
                        Field field = classFields.getField(entry.getKey());
                        if (child.size() > i) {
                            setFieldValue(obj, field, childArray[i]);
                        }
                    }
                    result.add(obj);
                }
                return (T) result;

            } else {
                // Create an instance of the class
                T obj = clazz.getConstructor().newInstance();

                ClassFields classFields = ReflectUtils.getClassFields(clazz);
                // For each child node, recursively parse and set the value
                for (Map.Entry<String, ISchemaNode> entry : schemaChildren.entrySet()) {
                    String fieldName = entry.getKey();
                    ISchemaNode childSchema = entry.getValue();
                    ISchemaPath childPath = pathChildren.get(fieldName);

                    // Determine the field's type via reflection
                    if (!classFields.containsField(fieldName)) {
                        throw new IllegalArgumentException("Field not found: " + fieldName);
                    }
                    Field field = classFields.getField(fieldName);
                    Class<?> fieldType = classFields.getFieldType(fieldName);

                    // Recursively process the child object
                    Object childValue = evaluateObject(childSchema, childPath, ctx, fieldType);

                    // Set the field value
                    setFieldValue(obj, field, childValue);
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
            // array must be one element or empty
            return value.isEmpty() || (value.size() == 1 && schemaMatch(schemaNode, value.get(0)));
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
    private static void setFieldValue(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting field value", e);
        }
    }
}
