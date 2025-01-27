/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.api.ISchemaExample;
import com.roiocam.jsm.api.ISchemaNode;
import com.roiocam.jsm.facade.JSONTools;
import org.junit.jupiter.api.Test;

abstract class SchemaOperatorTest {

    abstract JSONTools createTools();

    //    @Test
    //    public void simple_case() {
    //        // Generate schema
    //        ISchemaNode schema = SchemaOperator.generateSchema(User.class);
    //        // Serialize schema to JSON
    //        System.out.println("Serialized schema.Schema:");
    //        Object serializableForm = schema.toSerializableFormat();
    //        String schemaJson = createTools().writeValueAsString(serializableForm, true);
    //        System.out.println(schemaJson);
    //
    //        // Generate example JSON
    //        System.out.println("Example JSON:");
    //        ISchemaExample exampleJson = schema.generateExample();
    //        Object serializableForm1 = exampleJson.toSerializableFormat();
    //        String exampleJsonString = createTools().writeValueAsString(serializableForm1, true);
    //        System.out.println(exampleJsonString);
    //
    //        String schemaPathJson =
    //                """
    //                {
    //                  "user" : {
    //                    "name" : "$.username",
    //                    "age" : "$.profile.age",
    //                    "email": "$.profile.email"
    //                  },
    //                  "token" : "$.token"
    //                }
    //                """;
    //        SchemaPath schemaPath = SchemaParser.parsePath(createTools(), schemaPathJson);
    //        Assertions.assertNotNull(schemaPath);
    //
    //        // example json
    //        String parseJson =
    //                """
    //                {
    //                    "token": "abc123",
    //                    "username": "John",
    //                    "profile": {
    //                        "displayName": "John Ivy",
    //                        "age": 30,
    //                        "email": "john.ivy@example.com"
    //                    }
    //                }
    //                """;
    //        SchemaValue schemaValue = SchemaOperator.evaluateValue(schema, schemaPath, parseJson);
    //        Object valueSerializableFormat = schemaValue.toSerializableFormat();
    //        String expectedJson =
    //                """
    //                {
    //                  "user" : {
    //                    "name" : "John",
    //                    "age" : 30,
    //                    "email": "john.ivy@example.com"
    //                  },
    //                  "token" : "abc123"
    //                }
    //                """;
    //
    //        Assertions.assertEquals(
    //                createTools().writeTree(createTools().readTree(expectedJson)),
    //                createTools().writeValueAsString(valueSerializableFormat));
    //
    //        User user = SchemaOperator.evaluateObject(schema, schemaPath, parseJson, User.class);
    //        Assertions.assertNotNull(user);
    //        Assertions.assertEquals("abc123", user.getToken());
    //        Assertions.assertNotNull(user.getUser());
    //        Assertions.assertEquals("John", user.getUser().getName());
    //        Assertions.assertEquals(30, user.getUser().getAge());
    //    }

    @Test
    public void complex_obj_case() {
        // Generate schema
        ISchemaNode schema = SchemaOperator.generateSchema(ComplexUser.class);
        // Serialize schema to JSON
        System.out.println("Serialized schema.Schema:");
        Object serializableForm = schema.toSerializableFormat();
        String schemaJson = createTools().writeValueAsString(serializableForm, true);
        System.out.println(schemaJson);

        // Generate example JSON
        System.out.println("Example JSON:");
        ISchemaExample exampleJson = schema.generateExample();
        Object serializableForm1 = exampleJson.toSerializableFormat();
        String exampleJsonString = createTools().writeValueAsString(serializableForm1, true);
        System.out.println(exampleJsonString);

        // Schema path
        String schemaPathJson =
                """
                {
                  "charValue" : "$.charValue",
                  "latitude" : "$.latitude",
                  "roles" : ["$.roles[*]"],
                  "active" : "$.active",
                  "friends" :  [{
                              "name" : "$.friends[*].name",
                              "age" : "$.friends[*].name",
                              "email" : "$.friends[*].name"
                            }],
                  "token" : "$.token",
                  "balance" : "$.balance",
                  "permissions" : ["$.permissions[*]"],
                  "shortValue" : "$.shortValue",
                  "id" : "$.id",
                  "age" : "$.age",
                  "timestamp" : "$.timestamp",
                  "longitude" : "$.longitude",
                  "byteValue" : "$.byteValue"
                }
                """;
        //        SchemaPath schemaPath = SchemaParser.parsePath(createTools(), schemaPathJson);
        //        Assertions.assertNotNull(schemaPath);
        //        Assertions.assertEquals(
        //                createTools().writeTree(createTools().readTree(schemaPathJson)),
        //                createTools().writeValueAsString(schemaPath.toSerializableFormat()));
        //
        //        // example json
        //        String parseJson =
        //                """
        //                {
        //                    "token": "abc123",
        //                    "balance": 100.0,
        //                    "age": 30,
        //                    "active": true,
        //                    "id": 1,
        //                    "timestamp": 1234567890,
        //                    "latitude": 1.0,
        //                    "longitude": 1.0,
        //                    "shortValue": 1,
        //                    "byteValue": 1,
        //                    "charValue": "a",
        //                    "roles": [1, 2, 3],
        //                    "friends": [
        //                        {
        //                            "name": "John",
        //                            "age": 30,
        //                            "email" : "john.ivy@example.com"
        //                        },
        //                        {
        //                            "name": "Jane",
        //                            "age": 25,
        //                            "email" : "john.ivy@example.com"
        //                        }
        //                    ],
        //                    "permissions": ["read", "write"]
        //                }
        //                """;
        //        SchemaValue schemaValue = SchemaOperator.evaluateValue(schema, schemaPath,
        // parseJson);
        //        System.out.println("Evaluate Value:");
        //        Object valueSerializableFormat = schemaValue.toSerializableFormat();
        //        System.out.println(createTools().writeValueAsString(valueSerializableFormat,
        // true));
        //
        //        String expectedJson =
        //                """
        //                {
        //                  "charValue" : "a",
        //                  "latitude" : 1.0,
        //                  "roles" : [1, 2, 3],
        //                  "active" : true,
        //                  "friends" : [
        //                    {
        //                      "name" : "John",
        //                      "age" : 30
        //                    },
        //                    {
        //                      "name" : "Jane",
        //                      "age" : 25
        //                    }
        //                  ],
        //                  "token" : "abc123",
        //                  "balance" : 100.0,
        //                  "permissions" : ["read", "write"],
        //                  "shortValue" : 1,
        //                  "id" : 1,
        //                  "age" : 30,
        //                  "timestamp" : 1234567890,
        //                  "longitude" : 1.0,
        //                  "byteValue" : 1
        //                }
        //                """;
        //        Assertions.assertEquals(
        //                createTools().writeTree(createTools().readTree(expectedJson)),
        //                createTools().writeValueAsString(valueSerializableFormat));
    }
}
