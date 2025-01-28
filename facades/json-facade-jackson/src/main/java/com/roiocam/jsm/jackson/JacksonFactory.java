/* (C)2025 */
package com.roiocam.jsm.jackson;

import java.util.EnumSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.auto.service.AutoService;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.roiocam.jsm.facade.JSONFactories;
import com.roiocam.jsm.facade.JSONFactory;
import com.roiocam.jsm.facade.JSONPathContext;
import com.roiocam.jsm.facade.JSONTools;

@AutoService(JSONFactory.class)
public class JacksonFactory implements JSONFactory, Comparable<JSONFactory> {

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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        return new JacksonTools(objectMapper);
    }

    @Override
    public JSONPathContext createPathContext(String json) {
        return new JacksonPathContext(JsonPath.parse(json));
    }

    @Override
    public int compareTo(JSONFactory o) {
        return this.getClass().getName().compareTo(o.getClass().getName());
    }
}
