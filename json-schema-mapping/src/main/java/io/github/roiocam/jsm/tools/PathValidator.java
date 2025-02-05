/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.facade.JSONPathContext;

public class PathValidator {

    public static String check(Object value) {
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.startsWith("$.") && strVal.length() > 2) {
                return strVal;
            }
            if (isConstant(strVal)) {
                return strVal;
            }
            throw new IllegalArgumentException("invalid schema value");
        }
        return null;
    }

    public static boolean isConstant(String value) {
        return value.matches(JSONPathContext.REGEX);
    }
}
