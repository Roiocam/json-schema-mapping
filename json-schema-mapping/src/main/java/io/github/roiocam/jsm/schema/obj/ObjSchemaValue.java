/* (C)2025 */
package io.github.roiocam.jsm.schema.obj;

import io.github.roiocam.jsm.api.ISchemaValue;

public class ObjSchemaValue<R extends ISchemaValue> extends ObjSchema<Object, R>
        implements ISchemaValue {

    public ObjSchemaValue(ISchemaValue parent) {
        super(null, parent);
    }
}
