/* (C)2025 */
package io.github.roiocam.jsm.tools;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.TreeSet;

import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.facade.JSONPathContext;

/**
 * Maintainer all schema operation, which only working with objects, won't work with json string.
 */
public class SchemaOperator {

    private static TreeSet<ISchemaOperator> operators;

    static {
        operators = new TreeSet<>(Comparator.comparingInt(ISchemaOperator::getPriority));
        ServiceLoader.load(ISchemaOperator.class).forEach(p -> operators.add(p));
    }

    public static void registerOperator(ISchemaOperator operator) {
        operators.add(operator);
    }

    /**
     * Get the operators registered with the SchemaOperator.
     *
     * @return
     */
    private static ISchemaOperator getOperator() {
        if (operators.isEmpty()) {
            throw new IllegalStateException("No Operator registered");
        }
        return operators.first();
    }

    /**
     * Generates a schema and example JSON from a Java object.
     *
     * @param obj the Java object
     * @return a schema.SchemaNode representing the schema
     */
    public static ISchemaNode generateSchema(Class<?> obj) {
        return getOperator().processObject(obj, null);
    }

    /**
     * Evaluate a schema JSON to a {@link ISchemaValue} by using @{@link ISchemaNode} and {@link ISchemaPath}
     *
     * @param schema the schema node
     * @param path   the schema path
     * @param json   the JSON string
     * @return
     */
    public static ISchemaValue evaluateValue(
            ISchemaNode schema, ISchemaPath path, JSONFactory factory, String json) {
        JSONPathContext ctx = factory.createPathContext(json);
        return getOperator().evaluateValue(schema, path, ctx, null);
    }

    /**
     * Evaluate a schema JSON to a Java Object by using @{@link ISchemaNode} and {@link ISchemaPath}
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
            return getOperator().evaluateObject(schema, path, ctx, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating object", e);
        }
    }

    /**
     * verify the {@link ISchemaPath} is match to schema {@link ISchemaNode}
     *
     * @param schema
     * @param path
     * @return
     */
    public static boolean schemaMatch(ISchemaNode schema, ISchemaPath path) {
        return getOperator().schemaMatch(schema, path);
    }
}
