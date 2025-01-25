/* (C)2025 */
package com.roiocam.jsm.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        return new JacksonTools(new ObjectMapper());
    }

    @Override
    public int compareTo(JSONToolsFactory o) {
        return this.getClass().getName().compareTo(o.getClass().getName());
    }
}
