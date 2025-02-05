/* (C)2025 */
package io.github.roiocam.jsm.facade;

public interface JSONTools {

    JSONNode readTree(String json);

    String writeTree(JSONNode node);

    default String writeValueAsString(Object serializableForm) {
        return this.writeValueAsString(serializableForm, false);
    }

    String writeValueAsString(Object serializableForm, boolean prettyPrint);

    <T> T readValue(String json, Class<T> valueType);
}
