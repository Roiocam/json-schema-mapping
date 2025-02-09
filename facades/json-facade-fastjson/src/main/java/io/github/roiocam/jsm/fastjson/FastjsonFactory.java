/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.util.Set;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import io.github.roiocam.jsm.api.ISchema;
import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.facade.JSONTools;
import org.reflections.Reflections;

public class FastjsonFactory implements JSONFactory {

    private JSONTools tools;

    @Override
    public JSONTools create() {
        if (tools == null) {
            synchronized (this) {
                if (tools == null) {
                    tools = new FastjsonTools();
                    Reflections reflections = new Reflections("io.github.roiocam.jsm");
                    Set<Class<? extends ISchema>> schemaImplementations =
                            reflections.getSubTypesOf(ISchema.class);
                    ISchemaCodec codec = new ISchemaCodec(tools);
                    ParserConfig parserConfig = ParserConfig.getGlobalInstance();
                    SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
                    for (Class<? extends ISchema> implClass : schemaImplementations) {
                        serializeConfig.put(implClass, codec);
                        parserConfig.putDeserializer(implClass, codec);
                    }
                }
            }
        }

        return tools;
    }

    @Override
    public JSONPathContext createPathContext(String json) {
        return new FastjsonPathContext(json);
    }
}
