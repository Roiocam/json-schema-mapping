/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONNode;
import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.schema.SchemaNode;
import com.roiocam.jsm.schema.SchemaPath;
import com.roiocam.jsm.schema.SchemaValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class ITTest {

    abstract JSONTools createTools();

    @Test
    public void test() {
        divider();
        // 1. generate schema from a Java object
        SchemaNode schema = SchemaOperator.generateSchema(User.class);
        System.out.println("Schema:");
        System.out.println(createTools().writeValueAsString(schema.toSerializableFormat(), true));
        divider();

        System.out.println("Schema Example:");
        System.out.println(
                createTools()
                        .writeValueAsString(schema.generateExample().toSerializableFormat(), true));
        divider();

        // 2. parse a JSON string to a SchemaPath
        String pathJson =
                """
                {
                  "user" : {
                    "name" : "$.username",
                    "age" : "$.profile.age",
                    "email" : "$.profile.email"
                  },
                  "token" : "$.token"
                }
                """;
        SchemaPath path = SchemaParser.parsePath(createTools(), pathJson);
        Assertions.assertNotNull(path);
        System.out.println("Schema Path:");
        System.out.println(createTools().writeValueAsString(path.toSerializableFormat(), true));
        divider();

        // 3. verify the SchemaPath is match to the SchemaNode
        Assertions.assertTrue(SchemaOperator.schemaMatch(schema, path));

        // 4.giving the outer json string, using the schema and schema path to parse the value
        String outerJson =
                """
                {
                  "username" : "John",
                  "profile" : {
                    "age" : 30,
                    "email" : "jacky.chen@example.com"
                  },
                  "token" : "123456"
                }
                """;
        JSONNode jsonNode = createTools().readTree(outerJson);
        System.out.println("Difference Structure:");
        System.out.println(createTools().writeTree(jsonNode));
        divider();

        SchemaValue value = SchemaOperator.parseValue(schema, path, outerJson);
        System.out.println("Parsed Value:");
        System.out.println(createTools().writeValueAsString(value.toSerializableFormat(), true));
        divider();

        // 6. parse the value to a Java object
        User user = SchemaOperator.parseObject(schema, path, outerJson, User.class);
        System.out.println("Parsed Object:");
        System.out.println(createTools().writeValueAsString(user, true));
        divider();
    }

    private static void divider() {
        System.out.print("-----------------------------------\n");
    }
}
