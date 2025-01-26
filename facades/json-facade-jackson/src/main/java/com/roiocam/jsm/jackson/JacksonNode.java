/* (C)2025 */
package com.roiocam.jsm.jackson;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.roiocam.jsm.facade.JSONNode;

public class JacksonNode implements JSONNode {
    private final JsonNode node;

    public JacksonNode(JsonNode node) {
        this.node = node;
    }

    public JsonNode getNode() {
        return node;
    }

    @Override
    public boolean isTextual() {
        return node.isTextual();
    }

    @Override
    public String asText() {
        return node.asText();
    }

    @Override
    public boolean isObject() {
        return node.isObject();
    }

    @Override
    public Iterator<Map.Entry<String, JSONNode>> fields() {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        return new Iterator<Map.Entry<String, JSONNode>>() {
            @Override
            public boolean hasNext() {
                return fields.hasNext();
            }

            @Override
            public Map.Entry<String, JSONNode> next() {
                Map.Entry<String, JsonNode> next = fields.next();
                return new Map.Entry<String, JSONNode>() {
                    @Override
                    public String getKey() {
                        return next.getKey();
                    }

                    @Override
                    public JSONNode getValue() {
                        return new JacksonNode(next.getValue());
                    }

                    @Override
                    public JSONNode setValue(JSONNode value) {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
