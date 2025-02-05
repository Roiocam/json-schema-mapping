/* (C)2025 */
package io.github.roiocam.jsm.gson;

import com.google.gson.Gson;
import io.github.roiocam.jsm.facade.JSONNode;
import io.github.roiocam.jsm.facade.JSONTools;

public class GsonTools implements JSONTools {
    private final Gson gson;

    public GsonTools(Gson gson) {
        this.gson = gson;
    }

    @Override
    public JSONNode readTree(String json) {
        // TODO
        return new GsonNode(gson.toJsonTree(json));
    }

    @Override
    public String writeTree(JSONNode node) {
        if (node instanceof GsonNode) {
            return ((GsonNode) node).getElement().toString();
        }
        return this.writeValueAsString(node, false);
    }

    @Override
    public String writeValueAsString(Object serializableForm, boolean prettyPrint) {
        if (prettyPrint) {
            return gson.toJson(serializableForm);
        } else {
            return gson.toJson(serializableForm);
        }
    }

    @Override
    public <T> T readValue(String json, Class<T> valueType) {
        return gson.fromJson(json, valueType);
    }
}
