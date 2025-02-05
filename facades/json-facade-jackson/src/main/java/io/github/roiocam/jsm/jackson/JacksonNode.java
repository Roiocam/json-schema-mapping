/* (C)2025 */
package io.github.roiocam.jsm.jackson;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.roiocam.jsm.facade.JSONNode;

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

    @Override
    public boolean isValue() {
        return node.isValueNode();
    }

    @Override
    public <T> T asValue() {
        switch (node.getNodeType()) {
            case NULL -> {
                return null;
            }
            case BOOLEAN -> {
                return (T) Boolean.valueOf(node.asBoolean());
            }
            case NUMBER -> {
                if (node.isInt() || node.isIntegralNumber()) {
                    return (T) Integer.valueOf(node.asInt());
                } else if (node.isLong()) {
                    return (T) Long.valueOf(node.asLong());
                } else if (node.isDouble()) {
                    return (T) Double.valueOf(node.asDouble());
                } else if (node.isBigDecimal()) {
                    return (T) node.decimalValue();
                } else if (node.isBigInteger()) {
                    return (T) node.bigIntegerValue();
                } else {
                    throw new UnsupportedOperationException(
                            "Unsupported number type: " + node.numberType());
                }
            }
            case STRING -> {
                return (T) node.asText();
            }
            case ARRAY ->
            // TODO: implement array
            throw new UnsupportedOperationException("Array not supported");
            case OBJECT ->
            // TODO: implement object
            throw new UnsupportedOperationException("Object not supported");
            default -> throw new UnsupportedOperationException(
                    "Unsupported value type: " + node.getNodeType());
        }
    }

    @Override
    public boolean isArray() {
        return node.isArray();
    }

    @Override
    public Iterator<JSONNode> elements() {
        Iterator<JsonNode> elements = node.elements();
        return new Iterator<JSONNode>() {
            @Override
            public boolean hasNext() {
                return elements.hasNext();
            }

            @Override
            public JSONNode next() {
                return new JacksonNode(elements.next());
            }
        };
    }
}
