/* (C)2025 */
package com.roiocam.jsm.schema.array;

import com.roiocam.jsm.api.ISchemaPath;

public class ArraySchemaPath extends ArraySchema<String, ISchemaPath> implements ISchemaPath {
    public ArraySchemaPath(ISchemaPath parent) {
        super(parent);
    }

    @Override
    public void addElement(ISchemaPath element) {
        super.addElement(element);
    }
}
