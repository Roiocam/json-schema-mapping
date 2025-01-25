/* (C)2025 */
package com.roiocam.jsm.schema;

import java.util.Map;

public class SchemaNode extends Schema<Class<?>> {

    public SchemaNode(Class<?> value) {
        super(value);
    }

    @Override
    public String getValueAsString(Class<?> value) {
        return value.getSimpleName().toLowerCase();
    }

    /**
     * Generates an example where all values are "$."
     *
     * @return Example JSON as a nested Map
     */
    public SchemaExample generateExample() {
        if (this.getChildren().isEmpty()) {
            return new SchemaExample();
        }

        // Nested node: recursively generate example for children
        SchemaExample example = new SchemaExample();
        for (Map.Entry<String, Schema<Class<?>>> entry : this.getChildren().entrySet()) {
            example.addChild(entry.getKey(), ((SchemaNode) entry.getValue()).generateExample());
        }
        return example;
    }
}
