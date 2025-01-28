/* (C)2025 */
package com.roiocam.jsm.schema.array;

import java.util.ArrayList;
import java.util.List;

import com.roiocam.jsm.api.ISchema;
import com.roiocam.jsm.schema.value.Schema;

public class ArraySchema<R extends ISchema> extends Schema<List<R>> {
    public ArraySchema(R parent) {
        super(new ArrayList<>(), parent);
    }

    @Override
    public Object toSerializableFormat() {
        List<Object> serializedArray = new ArrayList<>();
        for (R element : getValue()) {
            serializedArray.add(element.toSerializableFormat());
        }
        return serializedArray;
    }

    public void addElement(R element) {
        this.getValue().add(element);
    }
}
