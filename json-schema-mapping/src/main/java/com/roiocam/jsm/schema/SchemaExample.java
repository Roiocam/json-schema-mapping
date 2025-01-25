/* (C)2025 */
package com.roiocam.jsm.schema;

import java.util.Map;

public class SchemaExample extends Schema<String> {

    private static final String EXAMPLE = "$.";

    public SchemaExample(Schema<?> parent) {
        super(EXAMPLE, parent);
    }

    @Override
    public String getValueAsString(String value) {
        return value;
    }

    @Override
    protected void writeRootType(Map<String, Object> result) {
        // No root type for example
    }
}
