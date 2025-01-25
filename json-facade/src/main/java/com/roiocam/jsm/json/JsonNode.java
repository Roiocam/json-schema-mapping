/* (C)2025 */
package com.roiocam.jsm.json;

import java.util.Iterator;
import java.util.Map;

public interface JsonNode {
    boolean isTextual();

    String asText();

    boolean isObject();

    Iterator<Map.Entry<String, JsonNode>> fields();
}
