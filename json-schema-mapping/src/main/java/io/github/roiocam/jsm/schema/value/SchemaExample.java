/* (C)2025 */
package io.github.roiocam.jsm.schema.value;

import io.github.roiocam.jsm.api.ISchemaExample;

public class SchemaExample extends SchemaValue implements ISchemaExample {

    public static final String EXAMPLE = "$.";

    public SchemaExample(ISchemaExample parent) {
        super(EXAMPLE, parent);
    }
}
