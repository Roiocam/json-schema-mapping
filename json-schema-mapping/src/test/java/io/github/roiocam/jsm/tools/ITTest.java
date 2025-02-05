/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.facade.JSONNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class ITTest {

    abstract JSONFactory getFactory();

    @Test
    public void test() {
        divider();
        // 1. generate schema from a Java object
        ISchemaNode schema = SchemaOperator.generateSchema(User.class);
        System.out.println("Schema:");
        System.out.println(
                getFactory().create().writeValueAsString(schema.toSerializableFormat(), true));
        divider();

        System.out.println("Schema Example:");
        System.out.println(
                getFactory()
                        .create()
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
        ISchemaPath path = SchemaParser.parsePath(getFactory().create(), pathJson);
        Assertions.assertNotNull(path);
        System.out.println("Schema Path:");
        System.out.println(
                getFactory().create().writeValueAsString(path.toSerializableFormat(), true));
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
        JSONNode jsonNode = getFactory().create().readTree(outerJson);
        System.out.println("Difference Structure:");
        System.out.println(getFactory().create().writeTree(jsonNode));
        divider();

        ISchemaValue value = SchemaOperator.evaluateValue(schema, path, getFactory(), outerJson);
        System.out.println("Evaluate Value:");
        System.out.println(
                getFactory().create().writeValueAsString(value.toSerializableFormat(), true));
        divider();

        // 6. evaluate the value to a Java object
        User user =
                SchemaOperator.evaluateObject(schema, path, getFactory(), outerJson, User.class);
        System.out.println("Evaluate Object:");
        System.out.println(getFactory().create().writeValueAsString(user, true));
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
        ISchemaValue parsedValue = SchemaParser.parseValue(getFactory().create(), parseJson);
        Assertions.assertNotNull(parsedValue);
        System.out.println("Parsed Value:");
        System.out.println(
                getFactory().create().writeValueAsString(parsedValue.toSerializableFormat(), true));
        divider();

        //            // 8. flatten the key map
        //            Map<String, String> flattenKey = schema.toFlattenKeyMap();
        //            System.out.println("Flatten Key:");
        //            System.out.println(getFactory().create().writeValueAsString(flattenKey,
        // true));
        //            divider();
        //
        //            // 9. flatten the key map with prefix
        //            ISchemaNode flattenSchema = SchemaParser.parseFlattenKey(flattenKey,
        // User.class);
        //            Assertions.assertEquals(
        //
        // getFactory().create().writeValueAsString(flattenSchema.toSerializableFormat()),
        //
        // getFactory().create().writeValueAsString(schema.toSerializableFormat()));
        //
        //            // 10. flatten the key map with prefix
        //            String flattenMappingJson =
        //                    """
        //                    {
        //                      "user.age" : "$.profile.age",
        //                      "user.name" : "$.username",
        //                      "user.email" : "$.profile.email",
        //                      "token" : "$.token"
        //                    }
        //                    """;
        //
        //            Map<String, String> flattenMapping =
        // getFactory().create().readValue(flattenMappingJson,
        //     Map.class);
        //            System.out.println("Flatten Key Path:");
        //            System.out.println(getFactory().create().writeValueAsString(flattenMapping,
        // true));
        //            divider();
        //
        //            ISchemaPath flattenMappingPath =
        // SchemaParser.parseFlattenPath(flattenMapping);
        //            Assertions.assertEquals(
        //
        // getFactory().create().writeValueAsString(flattenMappingPath.toSerializableFormat()),
        //
        // getFactory().create().writeValueAsString(path.toSerializableFormat()));
    }

    private static void divider() {
        System.out.print("-----------------------------------\n");
    }
}
