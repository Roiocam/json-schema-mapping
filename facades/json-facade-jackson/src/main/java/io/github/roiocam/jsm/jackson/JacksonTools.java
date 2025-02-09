/* (C)2025 */
package io.github.roiocam.jsm.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.roiocam.jsm.facade.JSONNode;
import io.github.roiocam.jsm.facade.JSONTools;

public class JacksonTools implements JSONTools {

    private final ObjectMapper objectMapper;

    public JacksonTools(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
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
            return ((JacksonNode) node).getNode().toString();
        }
        return this.writeValueAsString(node, false);
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

    @Override
    public <T> T readValue(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
