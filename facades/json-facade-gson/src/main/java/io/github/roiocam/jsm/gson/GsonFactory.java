/* (C)2025 */
package io.github.roiocam.jsm.gson;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.facade.JSONTools;

@AutoService(JSONFactory.class)
public class GsonFactory implements JSONFactory {
    @Override
    public JSONTools create() {
        return new GsonTools(new Gson());
    }

    @Override
    public JSONPathContext createPathContext(String json) {
        return new GsonPathContext(JsonPath.parse(json));
    }
}
