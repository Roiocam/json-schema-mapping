/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.api.ISchemaExample;
import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.api.ISchemaPath;
import com.roiocam.jsm.api.ISchemaValue;
import com.roiocam.jsm.facade.JSONFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class SchemaParserTest {
    abstract JSONFactory getFactory();

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
        ISchemaNode schemaNode = SchemaParser.parseNode(getFactory().create(), schemaJson);

        Assertions.assertNotNull(schemaNode);
        Object nodeSerializableFormat = schemaNode.toSerializableFormat();
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(schemaJson)),
                getFactory().create().writeValueAsString(nodeSerializableFormat));

        // Generate Example JSON from schema.SchemaNode
        System.out.println("Example JSON:");
        ISchemaExample exampleJson = schemaNode.generateExample();

        // Serialize Example JSON
        Object exampleJsonSerializableFormat = exampleJson.toSerializableFormat();
        String exampleJsonString =
                getFactory().create().writeValueAsString(exampleJsonSerializableFormat, true);
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
        ISchemaPath schemaPath = SchemaParser.parsePath(getFactory().create(), schemaPathJson);
        Assertions.assertNotNull(schemaPath);

        Object pathSerializableForm = schemaPath.toSerializableFormat();
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(schemaPathJson)),
                getFactory().create().writeValueAsString(pathSerializableForm));
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
              "string_array": [
                "item1",
                "item2"
                ],
                "number_array": [
                789,
                101112
                ],
                "boolean_array": [
                true,
                false
                ],
                "string_null_array": [
                "item1",
                null],
                "object_array": [
                {
                  "nestedString": "nestedExample",
                  "nestedNumber": 456
                },
                {
                  "nestedString": "nestedExample2",
                  "nestedNumber": 789
                }
                ]
            }
            """;

        ISchemaValue schemaNode = SchemaParser.parseValue(getFactory().create(), schemaJson);
        Assertions.assertNotNull(schemaNode);
        System.out.println("Example JSON:");
        Object exampleJsonSerializableFormat = schemaNode.toSerializableFormat();
        System.out.println(
                getFactory().create().writeValueAsString(exampleJsonSerializableFormat, true));
    }
}
