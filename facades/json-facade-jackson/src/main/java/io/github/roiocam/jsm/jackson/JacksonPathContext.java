/* (C)2025 */
package io.github.roiocam.jsm.jackson;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;

import com.jayway.jsonpath.ReadContext;
import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.facade.JSONTools;

public class JacksonPathContext implements JSONPathContext {
    private final ReadContext ctx;
    private final JSONTools jsonTools;

    public JacksonPathContext(ReadContext ctx, JSONTools jsonTools) {
        this.ctx = ctx;
        this.jsonTools = jsonTools;
    }

    @Override
    public <T> T read(String path, Class<T> type) {
        Matcher matcher = PATTERN.matcher(path);
        if (matcher.find()) {
            String group = matcher.group(2);
            if (type.equals(String.class)) {
                return (T) group;
            }
            return jsonTools.readValue(group, type);
        }
        Object value = ctx.read(path, type);
        return (T) value;
    }

    @Override
    public <T, R> T readArray(String path, Class<T> type, Class<R> elementType) {
        Object read = ctx.read(path, type);
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
}
