/* (C)2025 */
package com.roiocam.jsm.api;

public interface ISchema {
    void setParent(ISchema parent);

    Object toSerializableFormat();
}
