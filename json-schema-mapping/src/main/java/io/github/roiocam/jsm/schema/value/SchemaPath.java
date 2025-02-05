/* (C)2025 */
package io.github.roiocam.jsm.schema.value;

import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.tools.PathValidator;

public class SchemaPath extends SchemaValue<String> implements ISchemaPath {

    public SchemaPath(Object value, ISchemaPath parent) {
        super(PathValidator.check(value), parent);
    }
}
