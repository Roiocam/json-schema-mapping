/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.roiocam.jsm.facade.JSONNode;

public class FastjsonNode implements JSONNode {
    private final JSON jsonObject;

    public FastjsonNode(JSON jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSON getJsonObject() {
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
        return jsonObject instanceof JSONObject;
    }

    @Override
    public Iterator<Map.Entry<String, JSONNode>> fields() {
        Map<String, JSONNode> collect =
                ((JSONObject) jsonObject)
                        .entrySet().stream()
                                .collect(
                                        Collectors.toMap(
                                                e -> e.getKey(),
                                                e -> NodeConverter.convert(e.getValue())));
        return collect.entrySet().iterator();
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public boolean isArray() {
        return jsonObject instanceof JSONArray;
    }

    @Override
    public Iterator<JSONNode> elements() {
        Iterator<Object> iterator = ((JSONArray) jsonObject).iterator();
        return new Iterator<JSONNode>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public JSONNode next() {
                return NodeConverter.convert(iterator.next());
            }
        };
    }

    @Override
    public String asValue() {
        throw new IllegalStateException("Fastjson node can not be value, it is always map");
    }
}
