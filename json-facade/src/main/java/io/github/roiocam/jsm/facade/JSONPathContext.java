/* (C)2025 */
package io.github.roiocam.jsm.facade;

import java.util.regex.Pattern;

public interface JSONPathContext {

    String REGEX = "^(!!!)(.+)(!!!)$";
    Pattern PATTERN = Pattern.compile(REGEX);

    <T> T read(String path, Class<T> type);

    <T, R> T readArray(String path, Class<T> type, Class<R> elementType);
}
