/* (C)2025 */
package com.roiocam.jsm.facade;

public interface JSONTools {

    JSONNode readTree(String json);

    String writeTree(JSONNode node);

    String writeValueAsString(Object serializableForm);

    String writeValueAsString(Object serializableForm, boolean prettyPrint);
}
