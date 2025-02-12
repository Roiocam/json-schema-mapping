/* (C)2025 */
package io.github.roiocam.jsm.jackson;

import java.util.EnumSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.auto.service.AutoService;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import io.github.roiocam.jsm.api.ISchema;
import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONFactories;
import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.facade.JSONTools;

@AutoService(JSONFactory.class)
public class JacksonFactory implements JSONFactory, Comparable<JSONFactory> {

    private JacksonTools tools;

    static {
        JSONFactories.register(new JacksonFactory());
        Configuration.setDefaults(
                new Configuration.Defaults() {

                    private final JsonProvider jsonProvider = new JacksonJsonProvider();
                    private final MappingProvider mappingProvider = new JacksonMappingProvider();

                    @Override
                    public JsonProvider jsonProvider() {
                        return jsonProvider;
                    }

                    @Override
                    public MappingProvider mappingProvider() {
                        return mappingProvider;
                    }

                    @Override
                    public Set<Option> options() {
                        return EnumSet.noneOf(Option.class);
                    }
                });
    }

    @Override
    public JSONTools create() {
        if (tools == null) {
            synchronized (this) {
                if (tools == null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
                    tools = new JacksonTools(objectMapper);
                    SimpleModule module = new SimpleModule();
                    module.addSerializer(ISchema.class, new SchemaSerializer());
                    module.addDeserializer(ISchemaNode.class, new SchemaNodeDeserializer(tools));
                    module.addDeserializer(ISchemaPath.class, new SchemaPathDeserializer(tools));
                    module.addDeserializer(ISchemaValue.class, new SchemaValueDeserializer(tools));
                    objectMapper.registerModule(module);
                }
            }
        }
        return tools;
    }

    @Override
    public JSONPathContext createPathContext(String json) {
        return new JacksonPathContext(JsonPath.parse(json), create());
    }

    @Override
    public int compareTo(JSONFactory o) {
        return this.getClass().getName().compareTo(o.getClass().getName());
    }
}
