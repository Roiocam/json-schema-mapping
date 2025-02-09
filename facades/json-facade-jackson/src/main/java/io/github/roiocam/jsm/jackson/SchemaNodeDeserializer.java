/* (C)2025 */
package io.github.roiocam.jsm.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.facade.JSONTools;
import io.github.roiocam.jsm.tools.SchemaParser;

public class SchemaNodeDeserializer extends JsonDeserializer<ISchemaNode> {
    private final JSONTools tools;

    public SchemaNodeDeserializer(JSONTools tools) {
        this.tools = tools;
    }

    @Override
    public ISchemaNode deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JacksonException {
        String json = p.readValueAsTree().toString();
        return SchemaParser.parseNode(tools, json);
    }
}
