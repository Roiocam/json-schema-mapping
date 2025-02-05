/* (C)2025 */
package io.github.roiocam.jsm.schema.obj;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.roiocam.jsm.api.ISchema;
import io.github.roiocam.jsm.schema.value.Schema;

public abstract class ObjSchema<T, R extends ISchema> extends Schema<T> {

    /**
     * Maintains the tree structure of the JSON schema
     */
    private Map<String, R> children;

    public ObjSchema(T value, ISchema parent) {
        super(value, parent);
        this.children = new HashMap<>();
    }

    public Map<String, R> getChildren() {
        return children;
    }

    public void addChild(String name, R child) {
        children.put(name, child);
    }

    @Override
    public Object toSerializableFormat() {
        // handle object fields
        Map<String, Object> result = new LinkedHashMap<>();
        if (getParent() == null) {
            this.writeRootType(result);
        }

        if (!this.children.isEmpty()) {
            // Nested node: recursively serialize children
            for (Map.Entry<String, R> entry : this.children.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toSerializableFormat());
            }
        }
        return result;
    }

    protected void writeRootType(Map<String, Object> result) {
        // No root type for object
    }
}
