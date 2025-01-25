/* (C)2025 */
package com.roiocam.jsm.schema;

public class SchemaValue extends Schema<String> {

    public SchemaValue(Object value) {
        super(valid(value));
    }

    @Override
    public String getValueAsString(String value) {
        return value;
    }

    private static String valid(Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("invalid schema value");
        }
        String strVal = (String) value;
        if (strVal.startsWith("$.") && strVal.length() > 2) {
            return strVal;
        }
        throw new IllegalArgumentException("invalid schema value");
    }
}
