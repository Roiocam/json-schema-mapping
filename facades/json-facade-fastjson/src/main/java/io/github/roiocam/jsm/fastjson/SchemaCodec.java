/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.io.IOException;
import java.lang.reflect.Type;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import io.github.roiocam.jsm.api.ISchema;

public class SchemaCodec implements ObjectSerializer {
    @Override
    public void write(
            JSONSerializer serializer,
            Object object,
            Object fieldName,
            Type fieldType,
            int features)
            throws IOException {
        ISchema schemaNode = (ISchema) object;
        Object serializableFormat = schemaNode.toSerializableFormat();
        serializer.write(serializableFormat);
    }
}
