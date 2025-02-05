/* (C)2025 */
package io.github.roiocam.jsm.facade;

import java.util.Iterator;
import java.util.Map;

public interface JSONNode {
    boolean isTextual();

    String asText();

    boolean isObject();

    Iterator<Map.Entry<String, JSONNode>> fields();

    boolean isValue();

    <T> T asValue();

    boolean isArray();

    Iterator<JSONNode> elements();
}
