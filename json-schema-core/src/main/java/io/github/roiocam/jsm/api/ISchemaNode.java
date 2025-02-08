/* (C)2025 */
package io.github.roiocam.jsm.api;

public interface ISchemaNode extends ISchema {

    default ISchemaExample generateExample() {
        return generateExample(null);
    }

    ISchemaExample generateExample(ISchemaExample parent);
}
