/* (C)2025 */
package io.github.roiocam.jsm.jackson;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.facade.JSONTools;

public class JacksonPathContext extends JSONPathContext {
    private final ReadContext ctx;
    private final JSONTools jsonTools;

    public JacksonPathContext(ReadContext ctx, JSONTools jsonTools) {
        this.ctx = ctx;
        this.jsonTools = jsonTools;
    }

    @Override
    protected <T> T jsonParseObject(String value, Class<T> type) {
        return jsonTools.readValue(value, type);
    }

    @Override
    protected <T> T jsonPathRead(String path, Class<T> type) {
        return readValue(path, type);
    }

    @Override
    protected <T, R> T jsonPathReadArray(String path, Class<T> type, Class<R> elementType) {
        Object read = readValue(path, type);
        if (read != null && !Collection.class.isAssignableFrom(type)) {
            throw new IllegalStateException("JSON Path read values does not an array.");
        }
        if (read == null) {
            if (Set.class.isAssignableFrom(type)) {
                return (T) Collections.emptySet();
            } else if (type.isArray()) {
                return (T) new Object[0];
            } else {
                return (T) Collections.emptyList();
            }
        }
        Collection<?> collection = (Collection<?>) read;
        for (Object ele : collection) {
            if (!ele.getClass().isAssignableFrom(elementType)) {
                throw new IllegalStateException("array element had the difference type");
            }
        }
        return (T) read;
    }

    private <T> T readValue(String path, Class<T> type) {
        try {
            return ctx.read(path, type);
        } catch (PathNotFoundException e) {
            return null;
        }
    }
}
