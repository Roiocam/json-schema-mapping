/* (C)2025 */
package io.github.roiocam.jsm.schema.value;

import io.github.roiocam.jsm.api.ISchemaExample;
import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.schema.SchemaTypeMetadata;

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
