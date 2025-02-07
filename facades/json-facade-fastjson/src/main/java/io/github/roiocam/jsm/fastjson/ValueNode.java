/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.util.Iterator;
import java.util.Map;

import io.github.roiocam.jsm.facade.JSONNode;

public class ValueNode implements JSONNode {
    private final Object object;

    public ValueNode(Object object) {
        this.object = object;
    }

    @Override
    public boolean isTextual() {
        return false;
    }

    @Override
    public String asText() {
        throw new IllegalStateException("ValueNode is not an textual");
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public Iterator<Map.Entry<String, JSONNode>> fields() {
        throw new IllegalStateException("ValueNode is not an object");
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public <T> T asValue() {
        return (T) object;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public Iterator<JSONNode> elements() {
        throw new IllegalStateException("ValueNode is not an array");
    }

    @Override
    public String toJSONString() {
        return object.toString();
    }
}
