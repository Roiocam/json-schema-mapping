/* (C)2025 */
package com.roiocam.jsm.schema.value;

import com.roiocam.jsm.api.ISchema;

public abstract class Schema<T> implements ISchema {

    /**
     * Schema Value
     */
    private T value;

    /**
     * Parent schema, used for reverse parse
     */
    private ISchema parent;

    public Schema(T value, ISchema parent) {
        this.value = value;
        this.parent = parent;
    }

    public T getValue() {
        return value;
    }

    public ISchema getParent() {
        return parent;
    }

    @Override
    public void setParent(ISchema parent) {
        this.parent = parent;
    }

    /**
     * Custom serialization logic to output the desired JSON format.
     */
    @Override
    public Object toSerializableFormat() {
        return this.value;
    }
}
