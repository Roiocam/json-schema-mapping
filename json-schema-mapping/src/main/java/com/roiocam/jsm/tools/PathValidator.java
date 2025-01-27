/* (C)2025 */
package com.roiocam.jsm.tools;

public class PathValidator {
    public static String check(Object value) {
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
