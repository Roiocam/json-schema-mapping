/* (C)2025 */
package com.roiocam.jsm.api;

import com.roiocam.jsm.schema.array.ArraySchema;
import com.roiocam.jsm.schema.obj.ObjSchema;
import com.roiocam.jsm.schema.value.Schema;

public interface IFactory<R, T> {

    <U extends Schema<?>> U createValue(R value, T parent);

    <U extends ObjSchema<?, ?>> U createObj(R value, T parent);

    <U extends ArraySchema<?, ?>> U createArray(R value, T parent);
}
