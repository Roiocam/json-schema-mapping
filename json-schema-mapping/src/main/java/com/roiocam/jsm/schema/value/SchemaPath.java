/* (C)2025 */
package com.roiocam.jsm.schema.value;

import com.roiocam.jsm.api.ISchemaPath;
import com.roiocam.jsm.tools.PathValidator;

public class SchemaPath extends SchemaValue<String> implements ISchemaPath {

    public SchemaPath(Object value, ISchemaPath parent) {
        super(PathValidator.check(value), parent);
    }
}
