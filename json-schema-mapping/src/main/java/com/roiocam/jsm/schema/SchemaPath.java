/* (C)2025 */
package com.roiocam.jsm.schema;

public class SchemaPath extends SchemaValue<String> {

    public SchemaPath(Object value, Schema<?> parent) {
        super(check(value), parent);
    }

    private static String check(Object value) {
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.startsWith("$.") && strVal.length() > 2) {
                return strVal;
            }
            throw new IllegalArgumentException("invalid schema value");
        }

        return null;
    }
}
