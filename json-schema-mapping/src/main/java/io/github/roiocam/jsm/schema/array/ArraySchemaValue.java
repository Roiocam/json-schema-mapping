/* (C)2025 */
package io.github.roiocam.jsm.schema.array;

import io.github.roiocam.jsm.api.ISchemaValue;

public class ArraySchemaValue extends ArraySchema<ISchemaValue> implements ISchemaValue {
    public ArraySchemaValue(ISchemaValue parent) {
        super(parent);
    }
}
