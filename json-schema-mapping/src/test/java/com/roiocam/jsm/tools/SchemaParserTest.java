/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.facade.JSONToolsFactories;
import com.roiocam.jsm.schema.SchemaExample;
import com.roiocam.jsm.schema.SchemaNode;
import com.roiocam.jsm.schema.SchemaPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchemaParserTest {

    @Test
    public void test() {

        JSONTools tools = JSONToolsFactories.create();
        String schemaJson =
                """
        {
          "user" : {
            "name" : "string",
            "age" : "int"
          },
          "token" : "string"
        }
        """;

        // Parse the schema JSON into a schema.SchemaNode
        SchemaNode schemaNode = SchemaParser.parseNode(tools, schemaJson);

        Assertions.assertNotNull(schemaNode);
        Object nodeSerializableFormat = schemaNode.toSerializableFormat();
        Assertions.assertEquals(
                schemaJson.trim(), tools.writeValueAsString(nodeSerializableFormat, true).trim());

        // Generate Example JSON from schema.SchemaNode
        System.out.println("Example JSON:");
        SchemaExample exampleJson = schemaNode.generateExample();

        // Serialize Example JSON
        Object exampleJsonSerializableFormat = exampleJson.toSerializableFormat();
        String exampleJsonString = tools.writeValueAsString(exampleJsonSerializableFormat, true);
        System.out.println(exampleJsonString);

        String schemaPathJson =
                """
                {
                  "user" : {
                    "name" : "$.username",
                    "age" : "$.profile.age"
                  },
                  "token" : "$.token"
                }
                """;
        SchemaPath schemaPath = SchemaParser.parsePath(tools, schemaPathJson);
        Assertions.assertNotNull(schemaPath);

        Object pathSerializableForm = schemaPath.toSerializableFormat();
        Assertions.assertEquals(
                schemaPathJson.trim(), tools.writeValueAsString(pathSerializableForm, true).trim());
    }
}
