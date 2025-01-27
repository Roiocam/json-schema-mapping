/* (C)2025 */
package com.roiocam.jsm.schema.obj;

import com.roiocam.jsm.api.ISchemaExample;

public class ObjSchemaExample<T> extends ObjSchemaValue<T> implements ISchemaExample {

    public ObjSchemaExample(ISchemaExample parent) {
        super(parent);
    }
}
