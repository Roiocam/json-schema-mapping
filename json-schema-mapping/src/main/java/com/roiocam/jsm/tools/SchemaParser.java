/* (C)2025 */
package com.roiocam.jsm.tools;

import java.util.Iterator;
import java.util.Map;

import com.roiocam.jsm.json.JSONTools;
import com.roiocam.jsm.json.JsonNode;
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
     * @throws Exception If JSON parsing or schema construction fails.
     */
    public static SchemaNode parse(JSONTools tools, String json) throws Exception {
        JsonNode rootNode = tools.readTree(json);
        return parseNode(rootNode);
    }

    /**
     * Recursively parses a JsonNode into a schema.SchemaNode.
     *
     * @param node The current JsonNode to parse.
     * @return A schema.SchemaNode representing the structure of the JSON node.
     */
    private static SchemaNode parseNode(JsonNode node) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String typeName = node.asText();
            Class<?> type = TYPE_MAPPING.get(typeName);

            if (type == null) {
                throw new IllegalArgumentException("Unknown type: " + typeName);
            }
            return new SchemaNode(type);
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            SchemaNode schemaNode = new SchemaNode(null);
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                schemaNode.addChild(field.getKey(), parseNode(field.getValue()));
            }
            return schemaNode;
        }

        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }
}
