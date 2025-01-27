/* (C)2025 */
package com.roiocam.jsm.schema.array;

import com.roiocam.jsm.api.ISchemaValue;

public class ArraySchemaValue<T> extends ArraySchema<T, ISchemaValue> implements ISchemaValue {
    public ArraySchemaValue(ISchemaValue parent) {
        super(parent);
    }
}
