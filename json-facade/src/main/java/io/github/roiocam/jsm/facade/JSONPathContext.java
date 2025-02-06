/* (C)2025 */
package io.github.roiocam.jsm.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.roiocam.jsm.facade.extend.ConditionalMapping;
import io.github.roiocam.jsm.facade.extend.DefaultMapping;
import io.github.roiocam.jsm.facade.extend.Mapping;

public abstract class JSONPathContext {

    public static final String REGEX = "^(!!!)(.+)(!!!)$";
    public static final String PATH_REGEX = "^\\?<([^>]+)>";
    public static final String MAPPING_REGEX = "\\{([^}]+)\\}";
    public static final String RAW = "$";
    Pattern PATTERN = Pattern.compile(REGEX);
    Pattern PATH_PATTERN = Pattern.compile(PATH_REGEX);
    Pattern MAPPING_PATTERN = Pattern.compile(MAPPING_REGEX);

    /**
     * Parse the JSON string value to the specified type
     */
    protected abstract <T> T jsonParseObject(String value, Class<T> type);

    /**
     * Read the JSON data at the specified path and convert it to the specified type
     */
    protected abstract <T> T jsonPathRead(String path, Class<T> type);

    /**
     * Read the JSON data at the specified path and convert it to the specified type
     */
    protected abstract <T, R> T jsonPathReadArray(String path, Class<T> type, Class<R> elementType);

    /**
     * Read the JSON data at the specified path and convert it to the specified type
     * @param path
     * @param type
     * @param elementType
     * @return
     * @param <T>
     * @param <R>
     */
    public <T, R> T readArray(String path, Class<T> type, Class<R> elementType) {
        // check if the path is a default expression
        if (PathValidator.isDefaultExpression(path)) {
            throw new UnsupportedOperationException(
                    "default expression is not supported for array");
        }

        // check if the path is a condition expression
        if (PathValidator.isConditionExpression(path)) {
            ConditionalMapping mapping = parse(path);
            String jsonPath = mapping.getJsonPath();
            // read the corresponding part of JSON data through jsonPath
            T readed = jsonPathReadArray(jsonPath, type, elementType);
            if (readed instanceof Collection<?>) {
                Stream<R> mappedStream =
                        ((Collection<?>) readed)
                                .stream().map(e -> mappingValue(elementType, mapping, e));
                if (Set.class.isAssignableFrom(type)) {
                    return (T) mappedStream.collect(Collectors.toSet());
                }
                return (T) mappedStream.collect(Collectors.toList());
            } else if (readed.getClass().isArray()) {
                Object[] array = (Object[]) readed;
                for (int i = 0; i < array.length; i++) {
                    array[i] = mappingValue(elementType, mapping, array[i]);
                }
                return (T) array;
            } else {
                throw new IllegalStateException("JSON Path read values does not an array.");
            }
        }

        // if the path is a normal jsonPath, read the data directly
        return jsonPathReadArray(path, type, elementType);
    }

    /**
     * Read the JSON data at the specified path and convert it to the specified type
     * @param path
     * @param type
     * @return
     * @param <T>
     */
    public <T> T read(String path, Class<T> type) {
        // check if the path is a default expression
        if (PathValidator.isDefaultExpression(path)) {
            Matcher matcher = PATTERN.matcher(path);
            if (matcher.find()) {
                String group = matcher.group(2);
                return jsonParseMapObject(group, group, type);
            }
        }

        // check if the path is a condition expression
        if (PathValidator.isConditionExpression(path)) {
            ConditionalMapping mapping = parse(path);
            String jsonPath = mapping.getJsonPath();
            // read the corresponding part of JSON data through jsonPath
            Object readed = jsonPathRead(jsonPath, Object.class);
            return mappingValue(type, mapping, readed);
        }

        // if the path is a normal jsonPath, read the data directly
        return jsonPathRead(path, type);
    }

    /**
     * Mapping the value according to the condition mapping
     */
    private <T> T mappingValue(Class<T> type, ConditionalMapping mapping, Object readed) {
        List<Mapping> mappings = mapping.getMappings();
        Mapping defaultMapping = null;
        // traverse all mappings: first judge whether the condition mapping is established
        for (Mapping m : mappings) {
            if (m.getCondition() == null) {
                defaultMapping = m;
                continue;
            }
            if (evalCondition(m.getCondition(), readed)) {
                // choose original value if result is empty
                return jsonParseMapObject(m.getResult(), readed, type);
            }
        }
        // if no condition matches, return the default mapping result
        if (defaultMapping == null) {
            throw new IllegalStateException(
                    "no matching condition and no default mapping provided.");
        }

        return jsonParseMapObject(defaultMapping.getResult(), readed, type);
    }

    /**
     * Parse the JSON string value to the specified type
     */
    private <T> T jsonParseMapObject(String mapResult, Object raw, Class<T> type) {
        if (RAW.equals(mapResult)) {
            mapResult = raw.toString();
        }
        if (type.equals(String.class)) {
            return (T) mapResult;
        }
        return jsonParseObject(mapResult, type);
    }

    /**
     * Parse the conditional expression string
     * Format: ?<path> {condition1:result1} {condition2:result2} {defaultResult}
     * @param expression
     * @return
     */
    private ConditionalMapping parse(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("expression is null or empty");
        }

        // 1. extract the JsonPath part that starts with "?<" and ends with ">"
        Matcher pathMatcher = PATH_PATTERN.matcher(expression.trim());
        if (!pathMatcher.find()) {
            throw new IllegalArgumentException("invalid expression: cannot extract JsonPath part");
        }
        String jsonPath = pathMatcher.group(1).trim();

        // 2. extract the mapping part in all curly braces {}
        Matcher mappingMatcher = MAPPING_PATTERN.matcher(expression);
        List<Mapping> mappings = new ArrayList<>();
        while (mappingMatcher.find()) {
            String content = mappingMatcher.group(1).trim();
            // if contains colon, the format is condition:result
            if (content.contains(":")) {
                String[] parts = content.split(":", 2);
                String condition = parts[0].trim();
                String result = parts[1].trim();
                mappings.add(new Mapping(condition, result));
            }
            // no colon, default mapping
            else {
                mappings.add(new DefaultMapping(content));
            }
        }

        // 3. check the mapping part: must have mappings and at least one condition mapping and only
        // one default mapping
        if (mappings.isEmpty()) {
            throw new IllegalArgumentException("invalid expression: no mappings");
        }

        int defaultCount = 0;
        int conditionCount = 0;
        for (Mapping m : mappings) {
            if (m instanceof DefaultMapping) {
                defaultCount++;
            } else {
                conditionCount++;
            }
        }
        if (defaultCount > 1) {
            throw new IllegalArgumentException(
                    "invalid expression: only one default mapping is allowed. current: "
                            + defaultCount);
        }
        if (conditionCount == 0) {
            throw new IllegalArgumentException(
                    "invalid expression: at least one condition mapping is required");
        }

        return new ConditionalMapping(jsonPath, mappings);
    }

    /**
     * Evaluate a single condition, assuming the condition format is:
     * "==0", "==-1", ">100", "<=50", etc.
     * Supported operators are: ==, !=, >, <, >=, <=
     */
    private boolean evalCondition(String condition, Object value) {
        condition = condition.trim();
        String operator = null;
        String constantStr = null;
        if (condition.startsWith("==")) {
            operator = "==";
            constantStr = condition.substring(2).trim();
        } else if (condition.startsWith("!=")) {
            operator = "!=";
            constantStr = condition.substring(2).trim();
        } else if (condition.startsWith(">=")) {
            operator = ">=";
            constantStr = condition.substring(2).trim();
        } else if (condition.startsWith("<=")) {
            operator = "<=";
            constantStr = condition.substring(2).trim();
        } else if (condition.startsWith(">")) {
            operator = ">";
            constantStr = condition.substring(1).trim();
        } else if (condition.startsWith("<")) {
            operator = "<";
            constantStr = condition.substring(1).trim();
        } else {
            throw new IllegalArgumentException("unsupported condition format: " + condition);
        }
        // try to compare as number
        try {
            double constant = Double.parseDouble(constantStr);
            double valueNum = Double.parseDouble(value.toString());
            switch (operator) {
                case "==":
                    return valueNum == constant;
                case "!=":
                    return valueNum != constant;
                case ">":
                    return valueNum > constant;
                case "<":
                    return valueNum < constant;
                case ">=":
                    return valueNum >= constant;
                case "<=":
                    return valueNum <= constant;
            }
        } catch (NumberFormatException e) {
            // if number parsing failed, compare as string
            String valueStr = value.toString();
            int cmp = valueStr.compareTo(constantStr);
            switch (operator) {
                case "==":
                    return valueStr.equals(constantStr);
                case "!=":
                    return !valueStr.equals(constantStr);
                case ">":
                    return cmp > 0;
                case "<":
                    return cmp < 0;
                case ">=":
                    return cmp >= 0;
                case "<=":
                    return cmp <= 0;
            }
        }
        return false;
    }
}
