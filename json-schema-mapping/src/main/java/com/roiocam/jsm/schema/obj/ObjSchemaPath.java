/* (C)2025 */
package com.roiocam.jsm.schema.obj;

import com.roiocam.jsm.api.ISchemaPath;

public class ObjSchemaPath extends ObjSchemaValue<String> implements ISchemaPath {

    public ObjSchemaPath(ISchemaPath parent) {
        super(parent);
    }
}
