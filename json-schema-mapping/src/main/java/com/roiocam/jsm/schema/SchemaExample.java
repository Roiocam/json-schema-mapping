/* (C)2025 */
package com.roiocam.jsm.schema;

public class SchemaExample extends Schema<String> {

    private static final String EXAMPLE = "$.";

    public SchemaExample() {
        super(EXAMPLE);
    }

    @Override
    public String getValueAsString(String value) {
        return value;
    }
}
