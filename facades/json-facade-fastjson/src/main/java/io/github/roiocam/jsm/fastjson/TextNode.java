/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.util.Iterator;
import java.util.Map;

import io.github.roiocam.jsm.facade.JSONNode;

public class TextNode implements JSONNode {
    private final String value;

    public TextNode(String value) {
        this.value = value;
    }

    @Override
    public boolean isTextual() {
        return true;
    }

    @Override
    public String asText() {
        return value;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public Iterator<Map.Entry<String, JSONNode>> fields() {
        throw new IllegalStateException("TextNode is not an object");
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public String asValue() {
        return value;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public Iterator<JSONNode> elements() {
        throw new IllegalStateException("TextNode is not an array");
    }

    @Override
    public String toJSONString() {
        return value;
    }
}
