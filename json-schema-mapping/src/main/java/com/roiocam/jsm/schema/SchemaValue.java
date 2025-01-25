/* (C)2025 */
package com.roiocam.jsm.schema;

import java.util.Map;

public class SchemaValue<T> extends Schema<T> {

    public SchemaValue(T value, Schema<?> parent) {
        super(value, parent);
    }

    @Override
    public Object getSerializableValue(T value) {
        return value;
    }

    @Override
    protected void writeRootType(Map<String, Object> result) {
        // No root type for example
    }
}
