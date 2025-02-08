/* (C)2025 */
package io.github.roiocam.jsm.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.roiocam.jsm.api.ISchema;

public class SchemaSerializer extends JsonSerializer<ISchema> {
    @Override
    public void serialize(ISchema value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        Object serializableFormat = value.toSerializableFormat();
        gen.writeObject(serializableFormat);
    }
}
