/* (C)2025 */
package com.roiocam.jsm.schema;

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
    private Schema<T> parent;

    /**
     * Maintains the tree structure of the JSON schema
     */
    private Map<String, Schema<T>> children;

    public Schema(T value) {
        this(value, null);
    }

    public Schema(T value, Schema<T> parent) {
        this.value = value;
        this.parent = parent;
        this.children = new HashMap<>();
    }

    public T getValue() {
        return value;
    }

    public Schema<T> getParent() {
        return parent;
    }

    public abstract String getValueAsString(T value);

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
        Map<String, Object> result = new LinkedHashMap<>();

        if (parent == null) {
            // Root node: Include the "type" key for the root class
            result.put("type", getRootTypeAsString());
        }

        if (this.children.isEmpty()) {
            // Leaf node: return the type as a simple string
            result.put("value", this.getValueAsString(this.value));
        } else {
            // Nested node: recursively serialize children
            for (Map.Entry<String, Schema<T>> entry : this.children.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toSerializableFormat());
            }
        }
        return result;
    }

    /**
     * Return the full class name of the root type.
     * This is used when the current node is the top-level (root) node.
     */
    private String getRootTypeAsString() {
        if (parent == null) {
            return this.getClass().getName(); // Return the full class name (with package)
        }
        return "No root type"; // Root node should have no parent
    }
}
