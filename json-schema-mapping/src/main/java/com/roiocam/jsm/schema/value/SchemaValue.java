/* (C)2025 */
package com.roiocam.jsm.schema.value;

import com.roiocam.jsm.api.ISchemaValue;

public class SchemaValue<T> extends Schema<T> implements ISchemaValue {

    public SchemaValue(T value, ISchemaValue parent) {
        super(value, parent);
    }
}
