/* (C)2025 */
package io.github.roiocam.jsm.api;

public interface ISchema {
    void setParent(ISchema parent);

    Object toSerializableFormat();
}
