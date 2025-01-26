/* (C)2025 */
package com.roiocam.jsm.tools;

import java.util.Map;

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

        // 4.giving the outer json string, using the schema and schema path to evaluate the value
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

        SchemaValue value = SchemaOperator.evaluateValue(schema, path, outerJson);
        System.out.println("Evaluate Value:");
        System.out.println(createTools().writeValueAsString(value.toSerializableFormat(), true));
        divider();

        // 6. evaluate the value to a Java object
        User user = SchemaOperator.evaluateObject(schema, path, outerJson, User.class);
        System.out.println("Evaluate Object:");
        System.out.println(createTools().writeValueAsString(user, true));
        divider();

        // 7. parse a JSON string to a SchemaValue
        String parseJson =
                """
                {
                    "token" : "123456",
                    "user" : {
                        "name" : "John",
                        "age" : 30,
                        "email" : "jacky.chen@example.com"
                    }
                }
                """;
        SchemaValue<?> parsedValue = SchemaParser.parseValue(createTools(), parseJson);
        Assertions.assertNotNull(parsedValue);
        System.out.println("Parsed Value:");
        System.out.println(
                createTools().writeValueAsString(parsedValue.toSerializableFormat(), true));
        divider();

        // 8. flatten the key map
        Map<String, String> flattenKey = schema.toFlattenKeyMap();
        System.out.println("Flatten Key:");
        System.out.println(createTools().writeValueAsString(flattenKey, true));
        divider();

        // 9. flatten the key map with prefix
        SchemaNode flattenSchema = SchemaParser.parseFlattenKey(flattenKey, User.class);
        Assertions.assertEquals(
                createTools().writeValueAsString(flattenSchema.toSerializableFormat()),
                createTools().writeValueAsString(schema.toSerializableFormat()));

        // 10. flatten the key map with prefix
        String flattenMappingJson =
                """
                {
                  "user.age" : "$.profile.age",
                  "user.name" : "$.username",
                  "user.email" : "$.profile.email",
                  "token" : "$.token"
                }
                """;

        Map<String, String> flattenMapping = createTools().readValue(flattenMappingJson, Map.class);
        System.out.println("Flatten Key Path:");
        System.out.println(createTools().writeValueAsString(flattenMapping, true));
        divider();

        SchemaPath flattenMappingPath = SchemaParser.parseFlattenPath(flattenMapping);
        Assertions.assertEquals(
                createTools().writeValueAsString(flattenMappingPath.toSerializableFormat()),
                createTools().writeValueAsString(path.toSerializableFormat()));
    }

    private static void divider() {
        System.out.print("-----------------------------------\n");
    }
}
