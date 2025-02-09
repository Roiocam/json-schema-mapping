/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONNode;

public interface ISchemaParser {

    int getPriority();

    ISchemaNode parseSchemaNode(JSONNode node, ISchemaNode parent, Class<?> valueType);

    ISchemaPath parseSchemaPath(JSONNode node, ISchemaPath parent);

    ISchemaValue parseSchemaValue(JSONNode node, ISchemaValue parent);
}
