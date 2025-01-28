/* (C)2025 */
package com.roiocam.jsm.schema.obj;

import com.roiocam.jsm.api.ISchemaValue;

public class ObjSchemaValue<R extends ISchemaValue> extends ObjSchema<Object, R>
        implements ISchemaValue {

    public ObjSchemaValue(ISchemaValue parent) {
        super(null, parent);
    }
}
