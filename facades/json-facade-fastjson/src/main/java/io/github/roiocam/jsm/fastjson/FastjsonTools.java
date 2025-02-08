/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.github.roiocam.jsm.api.ISchema;
import io.github.roiocam.jsm.facade.JSONNode;
import io.github.roiocam.jsm.facade.JSONTools;
import org.reflections.Reflections;

public class FastjsonTools implements JSONTools {

    static {
        Reflections reflections = new Reflections("io.github.roiocam.jsm");
        Set<Class<? extends ISchema>> implementations = reflections.getSubTypesOf(ISchema.class);
        SchemaCodec schemaCodec = new SchemaCodec();
        SerializeConfig globalInstance = SerializeConfig.getGlobalInstance();
        for (Class<? extends ISchema> implClass : implementations) {
            globalInstance.put(implClass, schemaCodec);
        }
    }

    @Override
    public JSONNode readTree(String json) {
        Object parse = JSON.parse(json);
        if (!(parse instanceof JSON)) {
            throw new IllegalStateException("Unknown type of JSON object");
        }
        return new FastjsonNode((JSON) parse);
    }

    @Override
    public String writeTree(JSONNode node) {
        if (node instanceof FastjsonNode) {
            return ((FastjsonNode) node).getJsonObject().toJSONString();
        }
        return writeValueAsString(node, false);
    }

    @Override
    public String writeValueAsString(Object serializableForm, boolean prettyPrint) {
        if (prettyPrint) {
            return JSON.toJSONString(
                    serializableForm,
                    SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.QuoteFieldNames);
        }
        return JSON.toJSONString(serializableForm);
    }

    @Override
    public <T> T readValue(String json, Class<T> valueType) {
        return JSON.parseObject(json, valueType);
    }
}
