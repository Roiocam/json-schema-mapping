/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.roiocam.jsm.facade.JSONNode;

public class NodeConverter {
    public static JSONNode convert(Object value) {
        if (value instanceof JSONObject) {
            return new FastjsonNode((JSONObject) value);
        } else if (value instanceof JSONArray) {
            return new FastjsonNode((JSONArray) value);
        } else if (value instanceof String) {
            return new TextNode(value.toString());
        } else {
            return new ValueNode(value);
        }
    }
}
