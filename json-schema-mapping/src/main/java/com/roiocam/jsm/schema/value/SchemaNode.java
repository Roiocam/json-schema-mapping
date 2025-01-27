/* (C)2025 */
package com.roiocam.jsm.schema.value;

import com.roiocam.jsm.api.ISchemaExample;
import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.schema.SchemaTypeMetadata;

public class SchemaNode extends Schema<Class<?>> implements ISchemaNode {

    public SchemaNode(Class<?> value, ISchemaNode parent) {
        super(value, parent);
    }

    @Override
    public Object toSerializableFormat() {
        SchemaTypeMetadata metadata = SchemaTypeMetadata.fromClass(getValue());
        if (metadata == null) {
            throw new UnsupportedOperationException("Unsupported class: " + getValue());
        }
        return metadata.getType();
    }

    /**
     * Generates an example where all values are "$."
     *
     * @return Example JSON as a nested Map
     */
    @Override
    public ISchemaExample generateExample(ISchemaExample parent) {
        return new SchemaExample(parent);
    }
}
