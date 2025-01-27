/* (C)2025 */
package com.roiocam.jsm.schema.array;

import java.util.List;

import com.roiocam.jsm.api.ISchemaExample;
import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.schema.value.Schema;

public class ArraySchemaNode extends Schema<ISchemaNode> implements ISchemaNode {

    public ArraySchemaNode(ISchemaNode value, ISchemaNode parent) {
        super(value, parent);
    }

    @Override
    public Object toSerializableFormat() {
        return List.of(getValue().toSerializableFormat());
    }

    /**
     * Generates an example where all values are "$."
     *
     * @return Example JSON as a nested Map
     */
    @Override
    public ISchemaExample generateExample(ISchemaExample parent) {
        ArraySchemaExample example = new ArraySchemaExample(parent);
        ISchemaNode value = getValue();
        ISchemaExample schema = value.generateExample(parent);
        example.addElement(schema);
        return example;
    }
}
