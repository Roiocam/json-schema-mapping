/* (C)2025 */
package io.github.roiocam.jsm.facade;

import static io.github.roiocam.jsm.facade.JSONPathContext.PATH_REGEX;
import static io.github.roiocam.jsm.facade.JSONPathContext.REGEX;

public class PathValidator {

    /**
     * Check if the value is a valid path
     * @param value
     * @return
     */
    public static String check(Object value) {
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.startsWith("$.") && strVal.length() > 2) {
                return strVal;
            }
            if (isConditionExpression(strVal)) {
                return strVal;
            }
            if (isDefaultExpression(strVal)) {
                return strVal;
            }
            throw new IllegalArgumentException("invalid schema value");
        }
        return null;
    }

    /**
     * Check if the expression is a condition expression
     * @param expression
     * @return
     */
    public static boolean isConditionExpression(String expression) {
        return expression.matches(PATH_REGEX + ".*");
    }

    /**
     * Check if the expression is a default expression
     * @param expression
     * @return
     */
    public static boolean isDefaultExpression(String expression) {
        return expression.matches(REGEX);
    }
}
