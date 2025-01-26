/* (C)2025 */
package com.roiocam.jsm.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.roiocam.jsm.facade.JSONNode;
import com.roiocam.jsm.facade.JSONTools;

public class FastjsonTools implements JSONTools {

    @Override
    public JSONNode readTree(String json) {
        return new FastjsonNode(JSON.parseObject(json));
    }

    @Override
    public String writeTree(JSONNode node) {
        return "";
    }

    @Override
    public String writeValueAsString(Object serializableForm) {
        return this.writeValueAsString(serializableForm, false);
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
}
