/* (C)2025 */
package com.roiocam.jsm.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.api.ISchemaPath;
import com.roiocam.jsm.api.ISchemaValue;
import com.roiocam.jsm.facade.JSONNode;
import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.schema.SchemaTypeMetadata;
import com.roiocam.jsm.schema.array.ArraySchemaNode;
import com.roiocam.jsm.schema.array.ArraySchemaPath;
import com.roiocam.jsm.schema.array.ArraySchemaValue;
import com.roiocam.jsm.schema.obj.ObjSchemaNode;
import com.roiocam.jsm.schema.obj.ObjSchemaPath;
import com.roiocam.jsm.schema.obj.ObjSchemaValue;
import com.roiocam.jsm.schema.value.SchemaNode;
import com.roiocam.jsm.schema.value.SchemaPath;
import com.roiocam.jsm.schema.value.SchemaValue;

/**
 * Parses a JSON structure into the all kind of Schema Object
 */
public class SchemaParser {

    /**
     * Parses a JSON structure into a {@link SchemaNode}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaNode parseNode(JSONTools tools, String json) {
        return parseNode(tools, json, null);
    }

    /**
     * Parses a JSON structure into a {@link SchemaNode}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaNode parseNode(JSONTools tools, String json, Class<?> valueType) {
        JSONNode rootNode = tools.readTree(json);
        return parseSchemaNode(rootNode, null, valueType);
    }

    private static ISchemaNode parseSchemaNode(
            JSONNode node, ISchemaNode parent, Class<?> valueType) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String value = node.asText();
            if (value == null) {
                return new SchemaNode(valueType, parent);
            } else {
                SchemaTypeMetadata metadata = SchemaTypeMetadata.fromString(value);
                return new SchemaNode(metadata.getClazz(), parent);
            }
        }

        if (node.isArray()) {
            // Array node: recursively parse children
            Iterator<JSONNode> elements = node.elements();
            List<ISchemaNode> arrayNode = new ArrayList<>();
            while (elements.hasNext()) {
                JSONNode element = elements.next();
                ISchemaNode eleNode = parseSchemaNode(element, null, null);
                arrayNode.add(eleNode);
            }
            if (arrayNode.size() != 1) {
                throw new IllegalArgumentException("Array node should have only one element");
            }
            ArraySchemaNode arraySchemaNode = new ArraySchemaNode(arrayNode.get(0), parent);
            arrayNode.get(0).setParent(arraySchemaNode);
            return arraySchemaNode;
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            ObjSchemaNode current = new ObjSchemaNode(parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                current.addChild(field.getKey(), parseSchemaNode(field.getValue(), current, null));
            }
            return current;
        }

        if (node.isValue()) {
            throw new UnsupportedOperationException(
                    "node can not be a value, only type text or object or array is allowed");
        }

        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }

    /**
     * Parses a JSON structure into a {@link SchemaPath}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaPath parsePath(JSONTools tools, String json) {
        JSONNode rootNode = tools.readTree(json);
        return parseSchemaPath(rootNode, null);
    }

    private static ISchemaPath parseSchemaPath(JSONNode node, ISchemaPath parent) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String value = node.asText();
            return new SchemaPath(value, parent);
        }

        if (node.isArray()) {
            ArraySchemaPath path = new ArraySchemaPath(parent);
            // Array node: recursively parse children
            Iterator<JSONNode> elements = node.elements();
            while (elements.hasNext()) {
                JSONNode element = elements.next();
                ISchemaPath ele = parseSchemaPath(element, path);
                path.addElement(ele);
            }
            return path;
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            ObjSchemaPath current = new ObjSchemaPath(parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                current.addChild(field.getKey(), parseSchemaPath(field.getValue(), current));
            }
            return current;
        }

        if (node.isValue()) {
            // Leaf node: resolve type
            throw new UnsupportedOperationException(
                    "path can not be a value, only path text or object or array is allowed");
        }
        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }

    /**
     * Parses a JSON structure into a {@link SchemaValue}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaValue parseValue(JSONTools tools, String json) {
        JSONNode rootNode = tools.readTree(json);
        return parseSchemaValue(rootNode, null);
    }

    private static ISchemaValue parseSchemaValue(JSONNode node, ISchemaValue parent) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String value = node.asText();
            return new SchemaValue<>(value, parent);
        }

        if (node.isValue()) {
            return new SchemaValue<>(node.asValue(), parent);
        }

        if (node.isArray()) {
            ArraySchemaValue schemaValue = new ArraySchemaValue(parent);
            // Array node: recursively parse children
            Iterator<JSONNode> elements = node.elements();
            while (elements.hasNext()) {
                JSONNode element = elements.next();
                ISchemaValue ele = parseSchemaValue(element, schemaValue);
                schemaValue.addElement(ele);
            }
            return schemaValue;
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            ObjSchemaValue current = new ObjSchemaValue(parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                current.addChild(field.getKey(), parseSchemaValue(field.getValue(), current));
            }
            return current;
        }

        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }

    //    /**
    //     * Parses a flatten key into a SchemaNode.
    //     * @param flattenKey
    //     * @return
    //     */
    //    public static SchemaNode parseFlattenKey(Map<String, String> flattenKey) {
    //        return parseFlattenKey(flattenKey, null);
    //    }
    //
    //    /**
    //     * Parses a flatten key into a SchemaNode.
    //     * @param flattenKey
    //     * @param valueType
    //     * @return
    //     */
    //    public static SchemaNode parseFlattenKey(Map<String, String> flattenKey, Class<?>
    // valueType) {
    //        return parseFlattenSchema(
    //                flattenKey,
    //                (v, p) -> {
    //                    if (v == null) {
    //                        return new SchemaNode(valueType, p);
    //                    } else {
    //                        SchemaTypeMetadata metadata = SchemaTypeMetadata.fromString(v);
    //                        return new SchemaNode(metadata.getClazz(), p);
    //                    }
    //                });
    //    }
    //
    //    /**
    //     * Parses a flatten key mapping into a SchemaPath.
    //     * @param flattenMapping
    //     * @return
    //     */
    //    public static SchemaPath parseFlattenPath(Map<String, String> flattenMapping) {
    //        return parseFlattenSchema(flattenMapping, SchemaPath::new);
    //    }

    //    /**
    //     * Parse flatten key into a pattern in a loop.
    //     * @param flattenMapping
    //     * @param constructor
    //     * @return
    //     * @param <T>
    //     * @param <R>
    //     */
    //    private static <T extends Schema, R> T parseFlattenSchema(
    //            Map<String, R> flattenMapping, BiFunction<R, T, T> constructor) {
    //
    //        T root = constructor.apply(null, null);
    //
    //        for (Map.Entry<String, R> entry : flattenMapping.entrySet()) {
    //            String key = entry.getKey();
    //            String[] keys = key.split("\\.");
    //
    //            T current = root;
    //            for (int i = 0; i < keys.length; i++) {
    //                String part = keys[i];
    //
    //                // If this is the last part, add the final SchemaNode with the type
    //                if (i == keys.length - 1) {
    //                    current.addChild(part, constructor.apply(entry.getValue(), current));
    //                } else {
    //                    // Navigate or create intermediate nodes
    //                    final T finalCurrent = current;
    //                    Map<String, Schema<?,?>> children = current.getChildren();;
    //                    current =
    //                            (T)
    //                                    children.computeIfAbsent(
    //                                            part, k -> constructor.apply(null, finalCurrent));
    //                }
    //            }
    //        }
    //
    //        return root;
    //    }
}
