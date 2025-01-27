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

    //    public abstract Object getSerializableValue(T value);

    //    public R getArrayValue() {
    //        return arrayValue;
    //    }
    //
    //    public <U> void addArrayValue(U arrayValue){
    //        this.arrayValue = this.applyAddArrayValue(this.arrayValue, arrayValue);
    //    }

    //    protected abstract <U> R applyAddArrayValue(R current, U newAddition);

    //    protected abstract Object arrayValueToSerializableFormat(R value);

    /**
     * Custom serialization logic to output the desired JSON format.
     */
    @Override
    public Object toSerializableFormat() {
        return this.value;
        //        if (parent != null) {
        //            return this.getSerializableValue(this.value);
        //        }
        //        throw new UnsupportedOperationException("Node can not be both of leaf and root");
    }
    //        // Leaf node: return the type as a simple string
    //        if (parent != null && this.children.isEmpty() && arrayValue == null) {
    //            return this.getSerializableValue(this.value);
    //        }
    //
    //        // handle array value
    //        if (arrayValue != null) {
    //            return arrayValueToSerializableFormat(arrayValue);
    //        }
    //
    //        // handle object fields
    //        Map<String, Object> result = new LinkedHashMap<>();
    //        if (parent == null) {
    //            this.writeRootType(result);
    //        }
    //
    //        if (!this.children.isEmpty()) {
    //            // Nested node: recursively serialize children
    //            for (Map.Entry<String, Schema<T, R>> entry : this.children.entrySet()) {
    //                result.put(entry.getKey(), entry.getValue().toSerializableFormat());
    //            }
    //        }
    //
    //        return result;
    //    }
    //
    //    public Map<String, String> toFlattenKeyMap() {
    //        return this.toFlattenKeyMap(null);
    //    }
    //
    //    private Map<String, String> toFlattenKeyMap(String parentKey) {
    //
    //        // Leaf node: return the type as a simple string
    //        if (parent != null && this.children.isEmpty()) {
    //            return Collections.emptyMap();
    //        }
    //
    //        Map<String, String> result = new HashMap<>();
    //        if (!this.children.isEmpty()) {
    //            // Nested node: recursively serialize children
    //            for (Map.Entry<String, Schema<T, R>> entry : this.children.entrySet()) {
    //                Schema<T, R> entryValue = entry.getValue();
    //                Map<String, String> flattenKeyMap =
    // entryValue.toFlattenKeyMap(entry.getKey());
    //                if (flattenKeyMap.isEmpty()) {
    //                    String value = null;
    //                    if (entryValue.getValue() != null) {
    //                        value =
    // entryValue.getSerializableValue(entryValue.getValue()).toString();
    //                    }
    //                    if (parentKey == null) {
    //                        result.put(entry.getKey(), value);
    //                    } else {
    //                        result.put(parentKey + "." + entry.getKey(), value);
    //                    }
    //                } else {
    //                    result.putAll(flattenKeyMap);
    //                }
    //            }
    //        }
    //        return result;
    //    }
}
