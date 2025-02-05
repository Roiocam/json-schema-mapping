/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import io.github.roiocam.jsm.facade.JSONPathContext;

public class FastjsonPathContext implements JSONPathContext {
    private final String json;

    public FastjsonPathContext(String json) {
        this.json = json;
    }

    @Override
    public <T> T read(String path, Class<T> type) {
        return JSONPath.read(json, path, type);
    }

    @Override
    public <T, R> T readArray(String path, Class<T> type, Class<R> elementType) {
        Object read = JSONPath.read(json, path);
        if (!(read instanceof JSONArray)) {
            throw new IllegalStateException("JSON Path read values does not an array.");
        }
        JSONArray array = (JSONArray) read;
        Collection<R> res;
        if (List.class.isAssignableFrom(type) || type.isArray() || type.equals(Collection.class)) {
            res = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(type)) {
            res = new HashSet<>();
        } else {
            throw new UnsupportedOperationException("Unsupported collection type");
        }

        for (Object ele : array) {
            if (!ele.getClass().isAssignableFrom(elementType)) {
                throw new IllegalStateException("array element had the difference type");
            }
            res.add((R) ele);
        }
        return (T) res;
    }
}
