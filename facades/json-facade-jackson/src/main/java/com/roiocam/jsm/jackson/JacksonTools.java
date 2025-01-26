/* (C)2025 */
package com.roiocam.jsm.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roiocam.jsm.facade.JSONNode;
import com.roiocam.jsm.facade.JSONTools;

public class JacksonTools implements JSONTools {

    private final ObjectMapper objectMapper;

    public JacksonTools(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JSONNode readTree(String json) {
        try {
            return new JacksonNode(objectMapper.readTree(json));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeTree(JSONNode node) {
        if (node instanceof JacksonNode) {
            return ((JacksonNode) node).getNode().toPrettyString();
        }
        return this.writeValueAsString(node, true);
    }

    @Override
    public String writeValueAsString(Object serializableForm) {
        return this.writeValueAsString(serializableForm, false);
    }

    @Override
    public String writeValueAsString(Object serializableForm, boolean prettyPrint) {

        try {
            if (prettyPrint) {
                return objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(serializableForm);
            } else {
                return objectMapper.writeValueAsString(serializableForm);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
