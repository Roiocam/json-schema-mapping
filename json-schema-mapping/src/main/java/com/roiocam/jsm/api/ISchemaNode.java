/* (C)2025 */
package com.roiocam.jsm.api;

public interface ISchemaNode extends ISchema {

    default ISchemaExample generateExample() {
        return generateExample(null);
    }

    ISchemaExample generateExample(ISchemaExample parent);
}
