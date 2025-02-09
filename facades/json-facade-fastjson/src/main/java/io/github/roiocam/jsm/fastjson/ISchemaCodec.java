/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.io.IOException;
import java.lang.reflect.Type;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import io.github.roiocam.jsm.api.ISchema;
import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONTools;
import io.github.roiocam.jsm.tools.SchemaParser;

public class ISchemaCodec implements ObjectSerializer, ObjectDeserializer {

    private final JSONTools jsonTools;

    public ISchemaCodec(JSONTools jsonTools) {
        this.jsonTools = jsonTools;
    }

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

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String json = parser.parseObject(String.class);
        if (type instanceof Class<?>) {
            Class<?> clz = (Class<?>) type;
            if (ISchemaNode.class.isAssignableFrom(clz)) {
                return (T) SchemaParser.parseNode(jsonTools, json);
            } else if (ISchemaPath.class.isAssignableFrom(clz)) {
                return (T) SchemaParser.parsePath(jsonTools, json);
            } else if (ISchemaValue.class.isAssignableFrom(clz)) {
                return (T) SchemaParser.parseValue(jsonTools, json);
            }
        }

        throw new IllegalStateException(
                "Current type is not supported, but registered as Deserializer");
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
