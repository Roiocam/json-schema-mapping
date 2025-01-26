/* (C)2025 */
package com.roiocam.jsm.tools;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

import com.roiocam.jsm.facade.JSONNode;
import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.schema.Schema;
import com.roiocam.jsm.schema.SchemaNode;
import com.roiocam.jsm.schema.SchemaPath;
import com.roiocam.jsm.schema.SchemaTypeMetadata;
import com.roiocam.jsm.schema.SchemaValue;

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
    public static SchemaNode parseNode(JSONTools tools, String json) {
        return parseNode(tools, json, null);
    }

    /**
     * Parses a JSON structure into a {@link SchemaNode}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static SchemaNode parseNode(JSONTools tools, String json, Class<?> valueType) {
        JSONNode rootNode = tools.readTree(json);
        return parseSchema(
                rootNode,
                null,
                (v, p) -> {
                    if (v == null) {
                        return new SchemaNode(valueType, p);
                    } else {
                        SchemaTypeMetadata metadata =
                                SchemaTypeMetadata.fromString(String.valueOf(v));
                        return new SchemaNode(metadata.getClazz(), p);
                    }
                });
    }

    /**
     * Parses a JSON structure into a {@link SchemaPath}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static SchemaPath parsePath(JSONTools tools, String json) {
        JSONNode rootNode = tools.readTree(json);
        return parseSchema(rootNode, null, SchemaPath::new);
    }

    /**
     * Parses a JSON structure into a {@link SchemaPath}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static SchemaValue<?> parseValue(JSONTools tools, String json) {
        JSONNode rootNode = tools.readTree(json);
        return parseSchema(rootNode, null, SchemaValue::new);
    }

    /**
     * Recursively parses a JsonNode into a Schema.
     * @param node  The current JsonNode to parse.
     * @param parent The parent schema node.
     * @param constructor The constructor function to create a new schema node.
     * @return A schema node representing the structure of the JSON node.
     * @param <T> The type of the schema node.
     */
    private static <T extends Schema, R> T parseSchema(
            JSONNode node, T parent, BiFunction<R, T, T> constructor) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String value = node.asText();
            return constructor.apply((R) value, parent);
        }

        if (node.isValue()) {
            // Leaf node: resolve type
            return constructor.apply(node.asValue(), parent);
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            T current = constructor.apply(null, parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                current.addChild(
                        field.getKey(), parseSchema(field.getValue(), current, constructor));
            }
            return current;
        }

        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }

    public static SchemaNode parseFlattenKey(Map<String, String> flattenKey) {
        return parseFlattenKey(flattenKey, null);
    }

    public static SchemaNode parseFlattenKey(Map<String, String> flattenKey, Class<?> valueType) {
        SchemaNode root = new SchemaNode(valueType, null);

        for (Map.Entry<String, String> entry : flattenKey.entrySet()) {
            String key = entry.getKey();
            String[] keys = key.split("\\.");

            SchemaNode current = root;
            for (int i = 0; i < keys.length; i++) {
                String part = keys[i];

                // If this is the last part, add the final SchemaNode with the type
                if (i == keys.length - 1) {
                    Class<?> clazz = SchemaTypeMetadata.fromString(entry.getValue()).getClazz();
                    current.addChild(part, new SchemaNode(clazz, current));
                } else {
                    // Navigate or create intermediate nodes
                    final SchemaNode finalCurrent = current;
                    current =
                            (SchemaNode)
                                    current.getChildren()
                                            .computeIfAbsent(
                                                    part, k -> new SchemaNode(null, finalCurrent));
                }
            }
        }

        return root;
    }

    //    public static SchemaNode parseFlattenKey(Map<String, String> flattenKey, Class<?>
    // valueType) {
    //        SchemaNode current = new SchemaNode(valueType, null);
    //        // {
    //        //    "user.age" : "int",
    //        //    "user.name" : "string",
    //        //    "user.email" : "string",
    //        //    "token" : "string"
    //        //}
    //        // 先提前按 user 分组，转换为下面的形式
    //        // {
    //        //    "type" : "com.roiocam.jsm.tools.User",
    //        //    "user" : {
    //        //        "name" : "string",
    //        //        "age" : "int",
    //        //        "email" : "string"
    //        //    },
    //        //    "token" : "string"
    //        //}
    //        for (Map.Entry<String, String> entry : flattenKey.entrySet()) {
    //            String key = entry.getKey();
    //            String[] keys = key.split("\\.");
    //            if (keys.length == 1) {
    //                Class<?> clazz = SchemaTypeMetadata.fromString(entry.getValue()).getClazz();
    //                current.addChild(key, new SchemaNode(clazz, current));
    //            } else {
    //                // TODO: Implement nested schema
    //
    //            }
    //        }
    //        return null;
    //    }
}
