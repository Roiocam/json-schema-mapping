/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.jackson.JacksonToolsFactory;
import com.roiocam.jsm.schema.SchemaExample;
import com.roiocam.jsm.schema.SchemaNode;
import com.roiocam.jsm.schema.SchemaPath;
import com.roiocam.jsm.schema.SchemaValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchemaOperatorTest {
    @Test
    public void test() {
        JSONTools tools = new JacksonToolsFactory().create();
        // Generate schema
        SchemaNode schema = SchemaOperator.generateSchema(User.class);
        // Serialize schema to JSON
        System.out.println("Serialized schema.Schema:");
        Object serializableForm = schema.toSerializableFormat();
        String schemaJson = tools.writeValueAsString(serializableForm, true);
        System.out.println(schemaJson);

        // Generate example JSON
        System.out.println("Example JSON:");
        SchemaExample exampleJson = schema.generateExample();
        Object serializableForm1 = exampleJson.toSerializableFormat();
        String exampleJsonString = tools.writeValueAsString(serializableForm1, true);
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

        // example json
        String parseJson =
                """
                {
                    "token": "abc123",
                    "username": "John",
                    "profile": {
                        "displayName": "John Ivy",
                        "age": 30
                    }
                }
                """;
        SchemaValue schemaValue = SchemaOperator.parseValue(schema, schemaPath, parseJson);
        Object valueSerializableFormat = schemaValue.toSerializableFormat();
        String expectedJson =
                """
                {
                  "user" : {
                    "name" : "John",
                    "age" : 30
                  },
                  "token" : "abc123"
                }
                """;

        String valueJson = tools.writeValueAsString(valueSerializableFormat, true);
        Assertions.assertEquals(expectedJson.trim(), valueJson.trim());

        User user = SchemaOperator.parseObject(schema, schemaPath, parseJson, User.class);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("abc123", user.getToken());
        Assertions.assertNotNull(user.getUser());
        Assertions.assertEquals("John", user.getUser().getName());
        Assertions.assertEquals(30, user.getUser().getAge());
    }
}
