/* (C)2025 */
package com.roiocam.jsm.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.auto.service.AutoService;
import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.facade.JSONToolsFactories;
import com.roiocam.jsm.facade.JSONToolsFactory;

@AutoService(JSONToolsFactory.class)
public class JacksonToolsFactory implements JSONToolsFactory, Comparable<JSONToolsFactory> {

    static {
        JSONToolsFactories.register(new JacksonToolsFactory());
    }

    @Override
    public JSONTools create() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("    ", DefaultIndenter.SYS_LF));
        prettyPrinter.indentArraysWith(new DefaultIndenter("    ", DefaultIndenter.SYS_LF));
        objectMapper.setDefaultPrettyPrinter(prettyPrinter);

        return new JacksonTools(objectMapper);
    }

    @Override
    public int compareTo(JSONToolsFactory o) {
        return this.getClass().getName().compareTo(o.getClass().getName());
    }
}
