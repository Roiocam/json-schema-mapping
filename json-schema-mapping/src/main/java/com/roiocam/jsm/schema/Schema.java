/* (C)2025 */
package com.roiocam.jsm.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Schema<T> {

    /**
     * Schema Value
     */
    private T value;

    /**
     * Parent schema, used for reverse parse
     */
    private Schema<?> parent;

    /**
     * Maintains the tree structure of the JSON schema
     */
    private Map<String, Schema<T>> children;

    public Schema(T value, Schema<?> parent) {
        this.value = value;
        this.parent = parent;
        this.children = new HashMap<>();
    }

    public T getValue() {
        return value;
    }

    public Schema<?> getParent() {
        return parent;
    }

    public abstract Object getSerializableValue(T value);

    public Map<String, Schema<T>> getChildren() {
        return children;
    }

    public void addChild(String name, Schema child) {
        children.put(name, child);
    }

    /**
     * Custom serialization logic to output the desired JSON format.
     */
    public Object toSerializableFormat() {

        // Leaf node: return the type as a simple string
        if (parent != null && this.children.isEmpty()) {
            return this.getSerializableValue(this.value);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        if (parent == null) {
            this.writeRootType(result);
        }

        if (!this.children.isEmpty()) {
            // Nested node: recursively serialize children
            for (Map.Entry<String, Schema<T>> entry : this.children.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toSerializableFormat());
            }
        }

        return result;
    }

    public Map<String, String> toFlattenKeyMap() {
        return this.toFlattenKeyMap(null);
    }

    private Map<String, String> toFlattenKeyMap(String parentKey) {

        // Leaf node: return the type as a simple string
        if (parent != null && this.children.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();
        if (!this.children.isEmpty()) {
            // Nested node: recursively serialize children
            for (Map.Entry<String, Schema<T>> entry : this.children.entrySet()) {
                Schema<T> entryValue = entry.getValue();
                Map<String, String> flattenKeyMap = entryValue.toFlattenKeyMap(entry.getKey());
                if (flattenKeyMap.isEmpty()) {
                    String value = null;
                    if (entryValue.getValue() != null) {
                        value = entryValue.getSerializableValue(entryValue.getValue()).toString();
                    }
                    if (parentKey == null) {
                        result.put(entry.getKey(), value);
                    } else {
                        result.put(parentKey + "." + entry.getKey(), value);
                    }
                } else {
                    result.putAll(flattenKeyMap);
                }
            }
        }
        return result;
    }

    /**
     * Root node: Include the "type" key for the root class
     */
    protected void writeRootType(Map<String, Object> result) {
        String typeName;
        if (value == null) {
            typeName = null;
        } else if (value instanceof Class<?>) {
            typeName = ((Class<?>) value).getName();
        } else {
            typeName = value.toString();
        }
        result.put("type", typeName);
    }
}
