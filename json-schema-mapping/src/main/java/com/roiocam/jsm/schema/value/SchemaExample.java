/* (C)2025 */
package com.roiocam.jsm.schema.value;

import com.roiocam.jsm.api.ISchemaExample;

public class SchemaExample extends SchemaValue implements ISchemaExample {

    public static final String EXAMPLE = "$.";

    public SchemaExample(ISchemaExample parent) {
        super(EXAMPLE, parent);
    }
}
