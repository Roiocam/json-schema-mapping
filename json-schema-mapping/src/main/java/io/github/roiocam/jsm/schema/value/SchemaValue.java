/* (C)2025 */
package io.github.roiocam.jsm.schema.value;

import io.github.roiocam.jsm.api.ISchemaValue;

public class SchemaValue<T> extends Schema<T> implements ISchemaValue {

    public SchemaValue(T value, ISchemaValue parent) {
        super(value, parent);
    }
}
