/* (C)2025 */
package com.roiocam.jsm.fastjson;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.roiocam.jsm.facade.JSONNode;

public class FastjsonNode implements JSONNode {
    private final JSONObject jsonObject;

    public FastjsonNode(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public boolean isTextual() {
        return false;
    }

    @Override
    public String asText() {
        throw new IllegalStateException("Fastjson node can not be textual, it is always map");
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public Iterator<Map.Entry<String, JSONNode>> fields() {
        Map<String, JSONNode> collect =
                jsonObject.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        e -> e.getKey(),
                                        e -> {
                                            Object value = e.getValue();
                                            if (value instanceof JSONObject) {
                                                return new FastjsonNode((JSONObject) value);
                                            } else {
                                                return new TextNode(value.toString());
                                            }
                                        }));
        return collect.entrySet().iterator();
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public String asValue() {
        throw new IllegalStateException("Fastjson node can not be value, it is always map");
    }
}
