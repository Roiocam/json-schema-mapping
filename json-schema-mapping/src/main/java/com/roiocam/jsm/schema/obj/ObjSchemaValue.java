/* (C)2025 */
package com.roiocam.jsm.schema.obj;

import com.roiocam.jsm.api.ISchemaValue;

public class ObjSchemaValue<T> extends ObjSchema<T, ISchemaValue> implements ISchemaValue {

    public ObjSchemaValue(ISchemaValue parent) {
        super(parent);
    }
}
