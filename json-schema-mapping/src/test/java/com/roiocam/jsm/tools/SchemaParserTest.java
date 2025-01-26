/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.schema.SchemaExample;
import com.roiocam.jsm.schema.SchemaNode;
import com.roiocam.jsm.schema.SchemaPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class SchemaParserTest {
    abstract JSONTools createTools();

    @Test
    public void test() {
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
        SchemaNode schemaNode = SchemaParser.parseNode(createTools(), schemaJson);

        Assertions.assertNotNull(schemaNode);
        Object nodeSerializableFormat = schemaNode.toSerializableFormat();
        Assertions.assertEquals(
                createTools().writeTree(createTools().readTree(schemaJson)),
                createTools().writeValueAsString(nodeSerializableFormat));

        // Generate Example JSON from schema.SchemaNode
        System.out.println("Example JSON:");
        SchemaExample exampleJson = schemaNode.generateExample();

        // Serialize Example JSON
        Object exampleJsonSerializableFormat = exampleJson.toSerializableFormat();
        String exampleJsonString =
                createTools().writeValueAsString(exampleJsonSerializableFormat, true);
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
        SchemaPath schemaPath = SchemaParser.parsePath(createTools(), schemaPathJson);
        Assertions.assertNotNull(schemaPath);

        Object pathSerializableForm = schemaPath.toSerializableFormat();
        Assertions.assertEquals(
                createTools().writeTree(createTools().readTree(schemaPathJson)),
                createTools().writeValueAsString(pathSerializableForm));
    }
}
