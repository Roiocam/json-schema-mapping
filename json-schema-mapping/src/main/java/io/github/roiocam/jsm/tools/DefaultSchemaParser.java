/* (C)2025 */
package io.github.roiocam.jsm.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.auto.service.AutoService;
import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONNode;
import io.github.roiocam.jsm.schema.SchemaTypeMetadata;
import io.github.roiocam.jsm.schema.array.ArraySchemaNode;
import io.github.roiocam.jsm.schema.array.ArraySchemaPath;
import io.github.roiocam.jsm.schema.array.ArraySchemaValue;
import io.github.roiocam.jsm.schema.obj.ObjSchemaNode;
import io.github.roiocam.jsm.schema.obj.ObjSchemaPath;
import io.github.roiocam.jsm.schema.obj.ObjSchemaValue;
import io.github.roiocam.jsm.schema.value.SchemaNode;
import io.github.roiocam.jsm.schema.value.SchemaPath;
import io.github.roiocam.jsm.schema.value.SchemaValue;

@AutoService(ISchemaParser.class)
public class DefaultSchemaParser implements ISchemaParser {
    private static final DefaultSchemaParser INSTANCE = new DefaultSchemaParser();

    static {
        SchemaParser.registerParser(INSTANCE);
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public ISchemaNode parseSchemaNode(JSONNode node, ISchemaNode parent, Class<?> valueType) {

        if (node.isTextual()) {
            // Leaf node: resolve type
            String value = node.asText();
            if (value == null) {
                return new SchemaNode(valueType, parent);
            } else {
                SchemaTypeMetadata metadata = SchemaTypeMetadata.fromString(value);
                return new SchemaNode(metadata.getClazz(), parent);
            }
        }

        if (node.isArray()) {
            // Array node: recursively parse children
            Iterator<JSONNode> elements = node.elements();
            List<ISchemaNode> arrayNode = new ArrayList<>();
            while (elements.hasNext()) {
                JSONNode element = elements.next();
                ISchemaNode eleNode = parseSchemaNode(element, null, null);
                arrayNode.add(eleNode);
            }
            if (arrayNode.size() != 1) {
                throw new IllegalArgumentException("Array node should have only one element");
            }
            // TODO 拿到类型
            ArraySchemaNode arraySchemaNode =
                    new ArraySchemaNode(List.class, arrayNode.get(0), parent);
            arrayNode.get(0).setParent(arraySchemaNode);
            return arraySchemaNode;
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            ObjSchemaNode current = new ObjSchemaNode(valueType, parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                // TODO 拿到类型
                current.addChild(field.getKey(), parseSchemaNode(field.getValue(), current, null));
            }
            return current;
        }

        if (node.isValue()) {
            throw new UnsupportedOperationException(
                    "node can not be a value, only type text or object or array is allowed");
        }

        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }

    @Override
    public ISchemaPath parseSchemaPath(JSONNode node, ISchemaPath parent) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String value = node.asText();
            return new SchemaPath(value, parent);
        }

        if (node.isArray()) {
            ArraySchemaPath path = new ArraySchemaPath(parent);
            // Array node: recursively parse children
            Iterator<JSONNode> elements = node.elements();
            while (elements.hasNext()) {
                JSONNode element = elements.next();
                ISchemaPath ele = parseSchemaPath(element, path);
                path.addElement(ele);
            }
            return path;
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            ObjSchemaPath current = new ObjSchemaPath(parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                current.addChild(field.getKey(), parseSchemaPath(field.getValue(), current));
            }
            return current;
        }

        if (node.isValue()) {
            // Leaf node: resolve type
            throw new UnsupportedOperationException(
                    "path can not be a value, only path text or object or array is allowed");
        }
        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }

    @Override
    public ISchemaValue parseSchemaValue(JSONNode node, ISchemaValue parent) {
        if (node.isTextual()) {
            // Leaf node: resolve type
            String value = node.asText();
            return new SchemaValue<>(value, parent);
        }

        if (node.isValue()) {
            return new SchemaValue<>(node.asValue(), parent);
        }

        if (node.isArray()) {
            ArraySchemaValue schemaValue = new ArraySchemaValue(parent);
            // Array node: recursively parse children
            Iterator<JSONNode> elements = node.elements();
            while (elements.hasNext()) {
                JSONNode element = elements.next();
                ISchemaValue ele = parseSchemaValue(element, schemaValue);
                schemaValue.addElement(ele);
            }
            return schemaValue;
        }

        if (node.isObject()) {
            // Object node: recursively parse children
            ObjSchemaValue current = new ObjSchemaValue(parent);
            Iterator<Map.Entry<String, JSONNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JSONNode> field = fields.next();
                current.addChild(field.getKey(), parseSchemaValue(field.getValue(), current));
            }
            return current;
        }

        throw new IllegalArgumentException("Unsupported JSON structure: " + node);
    }
}
