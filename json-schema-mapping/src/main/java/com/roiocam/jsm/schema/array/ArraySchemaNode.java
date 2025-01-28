/* (C)2025 */
package com.roiocam.jsm.schema.array;

import java.util.List;

import com.roiocam.jsm.api.ISchemaExample;
import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.schema.value.Schema;

public class ArraySchemaNode extends Schema<Class<?>> implements ISchemaNode {

    private final ISchemaNode paramType;

    public ArraySchemaNode(Class<?> arrayType, ISchemaNode paramType, ISchemaNode parent) {
        super(arrayType, parent);
        this.paramType = paramType;
    }

    public ISchemaNode getParamType() {
        return paramType;
    }

    @Override
    public Object toSerializableFormat() {
        return List.of(getParamType().toSerializableFormat());
    }

    /**
     * Generates an example where all values are "$."
     *
     * @return Example JSON as a nested Map
     */
    @Override
    public ISchemaExample generateExample(ISchemaExample parent) {
        ArraySchemaExample example = new ArraySchemaExample(parent);
        ISchemaNode paramType = getParamType();
        ISchemaExample schema = paramType.generateExample(parent);
        example.addElement(schema);
        return example;
    }
}
