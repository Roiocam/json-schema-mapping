/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import io.github.roiocam.jsm.facade.JSONPathContext;

public class FastjsonPathContext extends JSONPathContext {
    private final String json;

    public FastjsonPathContext(String json) {
        this.json = json;
    }

    @Override
    protected <T> T jsonParseObject(String value, Class<T> type) {
        return JSON.parseObject(value, type);
    }

    @Override
    protected <T> T jsonPathRead(String path, Class<T> type) {
        return JSONPath.read(json, path, type);
    }

    @Override
    protected <T, R> T jsonPathReadArray(String path, Class<T> type, Class<R> elementType) {
        Object read = JSONPath.read(json, path);
        if (read != null && !(read instanceof JSONArray)) {
            throw new IllegalStateException("JSON Path read values does not an array.");
        }
        Collection<R> res;
        if (List.class.isAssignableFrom(type) || type.isArray() || type.equals(Collection.class)) {
            res = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(type)) {
            res = new HashSet<>();
        } else {
            throw new UnsupportedOperationException("Unsupported collection type");
        }
        if (read == null) {
            return (T) res;
        }
        JSONArray array = (JSONArray) read;
        for (Object ele : array) {
            if (!ele.getClass().isAssignableFrom(elementType)) {
                throw new IllegalStateException("array element had the difference type");
            }
            res.add((R) ele);
        }
        return (T) res;
    }
}
