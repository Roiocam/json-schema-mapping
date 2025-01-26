/* (C)2025 */
package com.roiocam.jsm.facade;

import java.util.Iterator;
import java.util.Map;

public interface JSONNode {
    boolean isTextual();

    String asText();

    boolean isObject();

    Iterator<Map.Entry<String, JSONNode>> fields();

    boolean isValue();

    <T> T asValue();
}
