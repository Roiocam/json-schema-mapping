/* (C)2025 */
package io.github.roiocam.jsm.tools;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.TreeSet;

import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONNode;
import io.github.roiocam.jsm.facade.JSONTools;

/**
 * Parses a JSON structure into the all kind of Schema Object
 */
public class SchemaParser {
    private static TreeSet<ISchemaParser> parsers;

    static {
        parsers = new TreeSet<>(Comparator.comparingInt(ISchemaParser::getPriority));
        ServiceLoader.load(ISchemaParser.class).forEach(p -> parsers.add(p));
    }

    public static void registerParser(ISchemaParser parser) {
        parsers.add(parser);
    }

    /**
     * Get the parser registered with the SchemaParser.
     *
     * @return
     */
    private static ISchemaParser getParser() {
        if (parsers.isEmpty()) {
            throw new IllegalStateException("No parser registered");
        }
        return parsers.first();
    }

    /**
     * Parses a JSON structure into a {@link ISchemaNode}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaNode parseNode(JSONTools tools, String json) {
        return parseNode(tools, json, null);
    }

    /**
     * Parses a JSON structure into a {@link ISchemaNode}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaNode parseNode(JSONTools tools, String json, Class<?> valueType) {
        JSONNode rootNode = tools.readTree(json);
        return getParser().parseSchemaNode(rootNode, null, valueType);
    }

    /**
     * Parses a JSON structure into a {@link ISchemaPath}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaPath parsePath(JSONTools tools, String json) {
        JSONNode rootNode = tools.readTree(json);
        return getParser().parseSchemaPath(rootNode, null);
    }

    /**
     * Parses a JSON structure into a {@link ISchemaValue}.
     *
     * @param json The JSON string representing the schema.
     * @return The root schema.SchemaNode representing the schema structure.
     */
    public static ISchemaValue parseValue(JSONTools tools, String json) {
        JSONNode rootNode = tools.readTree(json);
        return getParser().parseSchemaValue(rootNode, null);
    }
}
