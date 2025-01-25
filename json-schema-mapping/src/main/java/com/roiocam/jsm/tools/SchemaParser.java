/* (C)2025 */
package com.roiocam.jsm.tools;

import java.util.Iterator;
import java.util.Map;

import com.roiocam.jsm.facade.JSONNode;
import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.schema.SchemaNode;

public class SchemaParser {

    private static final Map<String, Class<?>> TYPE_MAPPING =
            Map.of(
                    "string", String.class,
                    "int", Integer.class,
                    "boolean", Boolean.class,
                    "double", Double.class,
                    "float", Float.class,
                    "long", Long.class
                    // TODO, char, by ,array 等类型
                    );

    /**
     * Parses a JSON structure into a schema.SchemaNode.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static SchemaNode parse(JSONTools tools, String json) {
        JSONNode rootNode = tools.readTree(json);
        return parseNode(rootNode, null);
    }

    /**
     * Recursively parses a JsonNode into a schema.SchemaNode.
     *
     * @param node The current JsonNode to parse.
     * @return A schema.SchemaNode representing the structure of the JSON node.
     */
    private static SchemaNode parseNode(JSONNode node, SchemaNode parent) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String typeName = node.asText();
            Class<?> type = TYPE_MAPPING.get(typeName);

            if (type == null) {
                throw new IllegalArgumentException("Unknown type: " + typeName);
            }
            return new SchemaNode(type, parent);
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            SchemaNode current = new SchemaNode(null, parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                current.addChild(field.getKey(), parseNode(field.getValue(), current));
            }
            return current;
        }

        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }
}
