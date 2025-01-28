/* (C)2025 */
package com.roiocam.jsm.schema.obj;

import java.util.Map;

import com.roiocam.jsm.api.ISchemaExample;
import com.roiocam.jsm.api.ISchemaNode;

public class ObjSchemaNode extends ObjSchema<Class<?>, ISchemaNode> implements ISchemaNode {

    public ObjSchemaNode(Class<?> clz, ISchemaNode parent) {
        super(clz, parent);
    }

    /**
     * Root node: Include the "type" key for the root class
     */
    @Override
    protected void writeRootType(Map<String, Object> result) {
        String typeName;
        if (getValue() == null) {
            typeName = null;
        } else if (getValue() instanceof Class<?>) {
            typeName = getValue().getName();
        } else {
            typeName = getValue().toString();
        }
        result.put("type", typeName);
    }

    /**
     * Generates an example where all values are "$."
     *
     * @return Example JSON as a nested Map
     */
    @Override
    public ISchemaExample generateExample(ISchemaExample parent) {
        // Nested node: recursively generate example for children
        ObjSchemaExample example = new ObjSchemaExample(parent);
        for (Map.Entry<String, ISchemaNode> entry : this.getChildren().entrySet()) {
            ISchemaNode value = entry.getValue();
            example.addChild(entry.getKey(), value.generateExample(example));
        }
        return example;
    }
}
