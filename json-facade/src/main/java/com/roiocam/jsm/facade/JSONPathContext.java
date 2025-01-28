/* (C)2025 */
package com.roiocam.jsm.facade;

public interface JSONPathContext {

    <T> T read(String path, Class<T> type);

    <T, R> T readArray(String path, Class<T> type, Class<R> elementType);
}
