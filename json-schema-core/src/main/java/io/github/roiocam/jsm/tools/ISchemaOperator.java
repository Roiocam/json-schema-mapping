/* (C)2025 */
package io.github.roiocam.jsm.tools;

import java.lang.reflect.InvocationTargetException;

import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONPathContext;

public interface ISchemaOperator {
    int getPriority();

    /**
     * Processes the Java object to generate schema and example data.
     *
     * @param clz the object class to process
     * @return a schema.SchemaNode representing the object
     */
    ISchemaNode processObject(Class<?> clz, ISchemaNode parent);

    ISchemaValue evaluateValue(
            ISchemaNode schema, ISchemaPath path, JSONPathContext ctx, ISchemaValue parent);

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
    <T> T evaluateObject(ISchemaNode schema, ISchemaPath path, JSONPathContext ctx, Class<T> clazz)
            throws NoSuchMethodException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException;

    boolean schemaMatch(ISchemaNode schema, ISchemaPath path);
}
