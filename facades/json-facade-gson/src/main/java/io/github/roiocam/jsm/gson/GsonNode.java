/* (C)2025 */
package io.github.roiocam.jsm.gson;

import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.roiocam.jsm.facade.JSONNode;

public class GsonNode implements JSONNode {
    private final JsonElement element;

    public GsonNode(JsonElement element) {
        this.element = element;
    }

    public JsonElement getElement() {
        return element;
    }

    @Override
    public boolean isTextual() {
        return (element instanceof JsonPrimitive) && ((JsonPrimitive) element).isString();
    }

    @Override
    public String asText() {
        return element.getAsString();
    }

    @Override
    public boolean isObject() {
        return element.isJsonObject();
    }

    @Override
    public Iterator<Map.Entry<String, JSONNode>> fields() {
        JsonObject jsonObject = element.getAsJsonObject();
        Map<String, JsonElement> map = jsonObject.asMap();
        Iterator<Map.Entry<String, JsonElement>> iterator = map.entrySet().iterator();
        return new Iterator<Map.Entry<String, JSONNode>>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Map.Entry<String, JSONNode> next() {
                Map.Entry<String, JsonElement> next = iterator.next();
                return new Map.Entry<String, JSONNode>() {
                    @Override
                    public String getKey() {
                        return next.getKey();
                    }

                    @Override
                    public JSONNode getValue() {
                        return new GsonNode(next.getValue());
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
        // TODO
        return !element.isJsonObject() && !element.isJsonArray();
    }

    @Override
    public <T> T asValue() {
        // TODO
        return null;
    }

    @Override
    public boolean isArray() {
        return element.isJsonArray();
    }

    @Override
    public Iterator<JSONNode> elements() {
        JsonArray jsonArray = element.getAsJsonArray();
        Iterator<JsonElement> iterator = jsonArray.iterator();
        return new Iterator<JSONNode>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public JSONNode next() {
                return new GsonNode(iterator.next());
            }
        };
    }
}
