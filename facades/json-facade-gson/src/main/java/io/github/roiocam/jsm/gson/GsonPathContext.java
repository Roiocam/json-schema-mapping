/* (C)2025 */
package io.github.roiocam.jsm.gson;

import java.util.Collection;

import com.jayway.jsonpath.ReadContext;
import io.github.roiocam.jsm.facade.JSONPathContext;

public class GsonPathContext implements JSONPathContext {
    private final ReadContext ctx;

    public GsonPathContext(ReadContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public <T> T read(String path, Class<T> type) {
        Object value = ctx.read(path, type);
        return (T) value;
    }

    @Override
    public <T, R> T readArray(String path, Class<T> type, Class<R> elementType) {
        Object read = ctx.read(path, type);
        if (!Collection.class.isAssignableFrom(type)) {
            throw new IllegalStateException("JSON Path read values does not an array.");
        }

        Collection<?> collection = (Collection<?>) read;
        for (Object ele : collection) {
            if (!ele.getClass().isAssignableFrom(elementType)) {
                throw new IllegalStateException("array element had the difference type");
            }
        }
        return (T) read;
    }
}
