/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.schema.SchemaExample;
import com.roiocam.jsm.schema.SchemaNode;
import com.roiocam.jsm.schema.SchemaPath;
import com.roiocam.jsm.schema.SchemaValue;
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

    @Test
    public void support_all_type_of_json() {
        String schemaJson =
                """
    {
      "string": "example",
      "number": 123,
      "boolean": true,
      "null": null,
      "object": {
        "nestedString": "nestedExample",
        "nestedNumber": 456
      },
      "array": [
        "item1",
        789,
        false,
        null,
        {
          "nestedObjectInArray": "value"
        }
      ]
    }
        """;

        SchemaValue schemaNode = SchemaParser.parseValue(createTools(), schemaJson);
        Assertions.assertNotNull(schemaNode);
        System.out.println("Example JSON:");
        Object exampleJsonSerializableFormat = schemaNode.toSerializableFormat();
        System.out.println(createTools().writeValueAsString(exampleJsonSerializableFormat, true));
    }
}
