/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.facade.JSONToolsFactories;
import com.roiocam.jsm.schema.SchemaExample;
import com.roiocam.jsm.schema.SchemaNode;
import org.junit.jupiter.api.Test;

class SchemaParserTest {

    @Test
    public void test() {

        JSONTools tools = JSONToolsFactories.create();
        String schemaJson =
                """
        {
          "token": "string",
          "user": {
            "name": "string",
            "age": "int"
          }
        }
        """;

        // Parse the schema JSON into a schema.SchemaNode
        SchemaNode schemaNode = SchemaParser.parse(tools, schemaJson);

        // Print the parsed schema.SchemaNode
        System.out.println("Parsed schema.SchemaNode:");
        System.out.println(schemaNode);

        // Generate Example JSON from schema.SchemaNode
        System.out.println("Example JSON:");
        SchemaExample exampleJson = schemaNode.generateExample();

        // Serialize Example JSON
        Object serializableForm = exampleJson.toSerializableFormat();
        String exampleJsonString = tools.writeValueAsString(serializableForm, true);
        System.out.println(exampleJsonString);
    }
}
