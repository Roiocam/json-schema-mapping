/* (C)2025 */
package com.roiocam.jsm.schema.map;

import java.util.Map;

import com.roiocam.jsm.api.ISchema;
import com.roiocam.jsm.api.ISchemaExample;
import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.schema.value.Schema;

public class MapSchemaNode extends Schema<Class<?>> implements ISchemaNode {

    private final ISchemaNode keyParamType;
    private final ISchemaNode valueParamType;

    public MapSchemaNode(
            Class<?> mapClz, ISchema parent, ISchemaNode keyParamType, ISchemaNode valueParamType) {
        super(mapClz, parent);
        this.keyParamType = keyParamType;
        this.valueParamType = valueParamType;
    }

    public ISchemaNode getKeyParamType() {
        return keyParamType;
    }

    public ISchemaNode getValueParamType() {
        return valueParamType;
    }

    @Override
    public Object toSerializableFormat() {
        return Map.of(
                getKeyParamType().toSerializableFormat(),
                getValueParamType().toSerializableFormat());
    }

    /**
     * Generates an example where all values are "$."
     *
     * @return Example JSON as a nested Map
     */
    @Override
    public ISchemaExample generateExample(ISchemaExample parent) {
        MapSchemaExample example = new MapSchemaExample(parent);
        example.putKV(
                getKeyParamType().generateExample(parent),
                getValueParamType().generateExample(parent));
        return example;
    }
}
