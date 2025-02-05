/* (C)2025 */
package com.roiocam.jsm.schema.map;

import java.util.HashMap;
import java.util.Map;

import com.roiocam.jsm.api.ISchema;
import com.roiocam.jsm.schema.value.Schema;

public class MapSchema<R extends ISchema> extends Schema<Map<R, R>> {

    public MapSchema(R parent) {
        super(new HashMap(), parent);
    }

    @Override
    public Object toSerializableFormat() {
        Map<Object, Object> serializedRes = new HashMap<>();
        Map<R, R> value = getValue();
        for (Map.Entry<R, R> kvEntry : value.entrySet()) {
            serializedRes.put(
                    kvEntry.getKey().toSerializableFormat(),
                    kvEntry.getValue().toSerializableFormat());
        }
        return serializedRes;
    }

    public void putKV(R key, R value) {
        this.getValue().put(key, value);
    }
}
