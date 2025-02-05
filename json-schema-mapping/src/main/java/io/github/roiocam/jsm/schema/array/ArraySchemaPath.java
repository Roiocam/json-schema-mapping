/* (C)2025 */
package io.github.roiocam.jsm.schema.array;

import io.github.roiocam.jsm.api.ISchemaPath;

public class ArraySchemaPath extends ArraySchema<ISchemaPath> implements ISchemaPath {
    public ArraySchemaPath(ISchemaPath parent) {
        super(parent);
    }

    @Override
    public void addElement(ISchemaPath element) {
        super.addElement(element);
    }
}
