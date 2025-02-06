/* (C)2025 */
package io.github.roiocam.jsm.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import io.github.roiocam.jsm.api.ISchemaExample;
import io.github.roiocam.jsm.api.ISchemaNode;
import io.github.roiocam.jsm.api.ISchemaPath;
import io.github.roiocam.jsm.api.ISchemaValue;
import io.github.roiocam.jsm.facade.JSONFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class SchemaOperatorTest {

    abstract JSONFactory getFactory();

    @Test
    public void simple_case() {
        // Generate schema
        ISchemaNode schema = SchemaOperator.generateSchema(User.class);
        // Serialize schema to JSON
        System.out.println("Serialized schema.Schema:");
        Object serializableForm = schema.toSerializableFormat();
        String schemaJson = getFactory().create().writeValueAsString(serializableForm, true);
        System.out.println(schemaJson);

        // Generate example JSON
        System.out.println("Example JSON:");
        ISchemaExample exampleJson = schema.generateExample();
        Object serializableForm1 = exampleJson.toSerializableFormat();
        String exampleJsonString =
                getFactory().create().writeValueAsString(serializableForm1, true);
        System.out.println(exampleJsonString);

        String schemaPathJson =
                """
                        {
                          "user" : {
                            "name" : "$.username",
                            "age" : "$.profile.age",
                            "email": "$.profile.email"
                          },
                          "token" : "$.token"
                        }
                        """;
        ISchemaPath schemaPath = SchemaParser.parsePath(getFactory().create(), schemaPathJson);
        Assertions.assertNotNull(schemaPath);

        // example json
        String parseJson =
                """
                        {
                            "token": "abc123",
                            "username": "John",
                            "profile": {
                                "displayName": "John Ivy",
                                "age": 30,
                                "email": "john.ivy@example.com"
                            }
                        }
                        """;
        ISchemaValue schemaValue =
                SchemaOperator.evaluateValue(schema, schemaPath, getFactory(), parseJson);
        Object valueSerializableFormat = schemaValue.toSerializableFormat();
        String expectedJson =
                """
                        {
                          "user" : {
                            "name" : "John",
                            "age" : 30,
                            "email": "john.ivy@example.com"
                          },
                          "token" : "abc123"
                        }
                        """;

        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(expectedJson)),
                getFactory().create().writeValueAsString(valueSerializableFormat));

        User user =
                SchemaOperator.evaluateObject(
                        schema, schemaPath, getFactory(), parseJson, User.class);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("abc123", user.getToken());
        Assertions.assertNotNull(user.getUser());
        Assertions.assertEquals("John", user.getUser().getName());
        Assertions.assertEquals(30, user.getUser().getAge());
    }

    @Test
    public void complex_obj_case() {
        // Generate schema
        ISchemaNode schema = SchemaOperator.generateSchema(ComplexUser.class);
        // Serialize schema to JSON
        System.out.println("Serialized schema.Schema:");
        Object serializableForm = schema.toSerializableFormat();
        String schemaJson = getFactory().create().writeValueAsString(serializableForm, true);
        System.out.println(schemaJson);

        // Generate example JSON
        System.out.println("Example JSON:");
        ISchemaExample exampleJson = schema.generateExample();
        Object serializableForm1 = exampleJson.toSerializableFormat();
        String exampleJsonString =
                getFactory().create().writeValueAsString(serializableForm1, true);
        System.out.println(exampleJsonString);

        // Schema path
        String schemaPathJson =
                """
                        {
                            "charValue": "$.config.charValue",
                            "latitude": "$.config.latitude",
                            "roles": ["$.roles[*].value"],
                            "active": "$.active",
                            "friends": [{
                                "name": "$.buddy[*].profile.name",
                                "age": "$.buddy[*].profile.age",
                                "email": "$.buddy[*].id"
                            }],
                            "token": "$.token",
                            "balance": "$.balance",
                            "permissions": ["$.permissions[*]"],
                            "shortValue": "$.config.shortValue",
                            "id": "$.id",
                            "age": "$.age",
                            "timestamp": "$.config.timestamp",
                            "longitude": "$.config.longitude",
                            "byteValue": "$.config.byteValue"
                        }
                        """;
        ISchemaPath schemaPath = SchemaParser.parsePath(getFactory().create(), schemaPathJson);
        Assertions.assertNotNull(schemaPath);
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(schemaPathJson)),
                getFactory().create().writeValueAsString(schemaPath.toSerializableFormat()));

        // schema, schemaPath
        boolean schemaMatch = SchemaOperator.schemaMatch(schema, schemaPath);
        Assertions.assertTrue(schemaMatch);

        String parseJson =
                """
                        {
                            "id": 1,
                            "age": 30,
                            "token": "abc123",
                            "balance": 100,
                            "active": true,
                            "config": {
                                "timestamp": 1234567890,
                                "latitude": 1.0,
                                "longitude": 1.0,
                                "shortValue": 1,
                                "byteValue": 1,
                                "charValue": "a"
                            },
                            "roles": [{
                                    "value": 1
                                },
                                {
                                    "value": 2
                                },
                                {
                                    "value": 3
                                }
                            ],
                            "buddy": [{
                                    "id": "john.ivy@example.com",
                                    "profile": {
                                        "name": "John",
                                        "age": 30
                                    }
                                },
                                {
                                    "id": "jane.li@company.com",
                                    "profile": {
                                        "name": "Jane",
                                        "age": 25
                                    }
                                }
                            ],
                            "permissions": [
                                "read",
                                "write"
                            ]
                        } """;
        ISchemaValue schemaValue =
                SchemaOperator.evaluateValue(schema, schemaPath, getFactory(), parseJson);
        System.out.println("Evaluate Value:");
        Object valueSerializableFormat = schemaValue.toSerializableFormat();
        System.out.println(getFactory().create().writeValueAsString(valueSerializableFormat, true));

        String expectedJson =
                """
                        {
                            "charValue": "a",
                            "latitude": 1.0,
                            "roles": [1, 2, 3],
                            "active": true,
                            "friends": [{
                                "name": "John",
                                "age": 30,
                                "email": "john.ivy@example.com"
                            }, {
                                "name": "Jane",
                                "age": 25,
                                "email": "jane.li@company.com"
                            }],
                            "token": "abc123",
                            "balance": 100,
                            "permissions": ["read", "write"],
                            "shortValue": 1,
                            "id": 1,
                            "age": 30,
                            "timestamp": 1234567890,
                            "longitude": 1.0,
                            "byteValue": 1
                        }
                        """;
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(expectedJson)),
                getFactory().create().writeValueAsString(valueSerializableFormat));

        ComplexUser complexUser =
                SchemaOperator.evaluateObject(
                        schema, schemaPath, getFactory(), parseJson, ComplexUser.class);
        System.out.println("Evaluate Object:");
        System.out.println(getFactory().create().writeValueAsString(complexUser, true));

        Assertions.assertNotNull(complexUser);
        Assertions.assertEquals(1, complexUser.getId());
        Assertions.assertEquals(new BigInteger(String.valueOf(30)), complexUser.getAge());
        Assertions.assertEquals("abc123", complexUser.getToken());
        Assertions.assertEquals(new BigDecimal(String.valueOf(100)), complexUser.getBalance());
        Assertions.assertTrue(complexUser.isActive());
        Assertions.assertEquals(1.0, complexUser.getLatitude());
        Assertions.assertEquals(1.0, complexUser.getLongitude());
        Assertions.assertEquals(1, complexUser.getShortValue());
        Assertions.assertEquals(1, complexUser.getByteValue());
        Assertions.assertEquals('a', complexUser.getCharValue());
        Assertions.assertArrayEquals(new int[] {1, 2, 3}, complexUser.getRoles());
        Assertions.assertEquals(1234567890, complexUser.getTimestamp());
        Set<String> read = Set.of("read", "write");
        Assertions.assertEquals(read.size(), complexUser.getPermissions().size());
        for (String permission : complexUser.getPermissions()) {
            Assertions.assertTrue(read.contains(permission));
        }
        Assertions.assertEquals(2, complexUser.getFriends().size());
        Assertions.assertEquals("John", complexUser.getFriends().get(0).getName());
        Assertions.assertEquals(30, complexUser.getFriends().get(0).getAge());
        Assertions.assertEquals("john.ivy@example.com", complexUser.getFriends().get(0).getEmail());
        Assertions.assertEquals("Jane", complexUser.getFriends().get(1).getName());
        Assertions.assertEquals(25, complexUser.getFriends().get(1).getAge());
        Assertions.assertEquals("jane.li@company.com", complexUser.getFriends().get(1).getEmail());
    }

    @Test
    public void allow_empty_path() {
        // Generate schema
        ISchemaNode schema = SchemaOperator.generateSchema(ComplexUser.class);

        // Schema path
        String schemaPathJson =
                """
                        {
                            "charValue": "$.config.charValue",
                            "latitude": "$.config.latitude",
                            "roles": [],
                            "active": "$.active",
                            "friends": [],
                            "token": "$.token",
                            "balance": "$.balance",
                            "permissions": [],
                            "shortValue": "$.config.shortValue",
                            "id": "$.id",
                            "age": "$.age",
                            "timestamp": "$.config.timestamp",
                            "longitude": "$.config.longitude",
                            "byteValue": "$.config.byteValue"
                        }
                        """;
        ISchemaPath schemaPath = SchemaParser.parsePath(getFactory().create(), schemaPathJson);
        Assertions.assertNotNull(schemaPath);
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(schemaPathJson)),
                getFactory().create().writeValueAsString(schemaPath.toSerializableFormat()));

        // schema, schemaPath
        boolean schemaMatch = SchemaOperator.schemaMatch(schema, schemaPath);
        Assertions.assertTrue(schemaMatch);

        String parseJson =
                """
                        {
                            "id": 1,
                            "age": 30,
                            "token": "abc123",
                            "balance": 100,
                            "active": true,
                            "config": {
                                "timestamp": 1234567890,
                                "latitude": 1.0,
                                "longitude": 1.0,
                                "shortValue": 1,
                                "byteValue": 1,
                                "charValue": "a"
                            },
                            "roles": [{
                                    "value": 1
                                },
                                {
                                    "value": 2
                                },
                                {
                                    "value": 3
                                }
                            ],
                            "buddy": [{
                                    "id": "john.ivy@example.com",
                                    "profile": {
                                        "name": "John",
                                        "age": 30
                                    }
                                },
                                {
                                    "id": "jane.li@company.com",
                                    "profile": {
                                        "name": "Jane",
                                        "age": 25
                                    }
                                }
                            ],
                            "permissions": [
                                "read",
                                "write"
                            ]
                        } """;
        ISchemaValue schemaValue =
                SchemaOperator.evaluateValue(schema, schemaPath, getFactory(), parseJson);
        Object valueSerializableFormat = schemaValue.toSerializableFormat();

        String expectedJson =
                """
                        {
                            "charValue": "a",
                            "latitude": 1.0,
                            "roles": [],
                            "active": true,
                            "friends": [],
                            "token": "abc123",
                            "balance": 100,
                            "permissions": [],
                            "shortValue": 1,
                            "id": 1,
                            "age": 30,
                            "timestamp": 1234567890,
                            "longitude": 1.0,
                            "byteValue": 1
                        }
                        """;
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(expectedJson)),
                getFactory().create().writeValueAsString(valueSerializableFormat));
    }

    @Test
    public void allow_default_value() {
        // Generate schema
        ISchemaNode schema = SchemaOperator.generateSchema(ComplexUser.class);

        // Schema path
        String schemaPathJson =
                """
                        {
                            "charValue": "$.config.charValue",
                            "latitude": "$.config.latitude",
                            "roles": [],
                            "active": "$.active",
                            "friends": [],
                            "token": "!!!token!!!",
                            "balance": "!!!100!!!",
                            "permissions": [],
                            "shortValue": "$.config.shortValue",
                            "id": "$.id",
                            "age": "!!!123!!!",
                            "timestamp": "!!!123123123!!!",
                            "longitude": "$.config.longitude",
                            "byteValue": "$.config.byteValue"
                        }
                        """;
        ISchemaPath schemaPath = SchemaParser.parsePath(getFactory().create(), schemaPathJson);
        Assertions.assertNotNull(schemaPath);
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(schemaPathJson)),
                getFactory().create().writeValueAsString(schemaPath.toSerializableFormat()));

        // schema, schemaPath
        boolean schemaMatch = SchemaOperator.schemaMatch(schema, schemaPath);
        Assertions.assertTrue(schemaMatch);

        String parseJson =
                """
                        {
                            "id": 1,
                            "age": 30,
                            "token": "abc123",
                            "balance": 100,
                            "active": true,
                            "config": {
                                "timestamp": 1234567890,
                                "latitude": 1.0,
                                "longitude": 1.0,
                                "shortValue": 1,
                                "byteValue": 1,
                                "charValue": "a"
                            },
                            "roles": [{
                                    "value": 1
                                },
                                {
                                    "value": 2
                                },
                                {
                                    "value": 3
                                }
                            ],
                            "buddy": [{
                                    "id": "john.ivy@example.com",
                                    "profile": {
                                        "name": "John",
                                        "age": 30
                                    }
                                },
                                {
                                    "id": "jane.li@company.com",
                                    "profile": {
                                        "name": "Jane",
                                        "age": 25
                                    }
                                }
                            ],
                            "permissions": [
                                "read",
                                "write"
                            ]
                        } """;
        ISchemaValue schemaValue =
                SchemaOperator.evaluateValue(schema, schemaPath, getFactory(), parseJson);
        Object valueSerializableFormat = schemaValue.toSerializableFormat();

        String expectedJson =
                """
                        {
                            "charValue": "a",
                            "latitude": 1.0,
                            "roles": [],
                            "active": true,
                            "friends": [],
                            "token": "token",
                            "balance": 100,
                            "permissions": [],
                            "shortValue": 1,
                            "id": 1,
                            "age": 123,
                            "timestamp": 123123123,
                            "longitude": 1.0,
                            "byteValue": 1
                        }
                        """;
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(expectedJson)),
                getFactory().create().writeValueAsString(valueSerializableFormat));
    }

    @Test
    public void allow_condition_path() {
        // Generate schema
        ISchemaNode schema = SchemaOperator.generateSchema(ComplexUser.class);

        // Schema path
        String schemaPathJson =
                """
                        {
                            "charValue": "$.config.charValue",
                            "latitude": "$.config.latitude",
                            "roles": ["$.roles[*].value"],
                            "active": "$.active",
                            "friends": [{
                                "name": "$.buddy[*].profile.name",
                                "age": "?<$.buddy[*].profile.age> {>=18: 18} {$}",
                                "email": "$.buddy[*].id"
                            }],
                            "token": "$.token",
                            "balance": "$.balance",
                            "permissions": ["$.permissions[*]"],
                            "shortValue": "$.config.shortValue",
                            "id": "?<$.id> {>1: 1} {<1: 0} {==1: 2} {3}",
                            "age":"?<$.age> {>=18: 18} {0}",
                            "timestamp": "$.config.timestamp",
                            "longitude": "$.config.longitude",
                            "byteValue": "$.config.byteValue"
                        }
                        """;
        ISchemaPath schemaPath = SchemaParser.parsePath(getFactory().create(), schemaPathJson);
        Assertions.assertNotNull(schemaPath);
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(schemaPathJson)),
                getFactory().create().writeValueAsString(schemaPath.toSerializableFormat()));

        // schema, schemaPath
        boolean schemaMatch = SchemaOperator.schemaMatch(schema, schemaPath);
        Assertions.assertTrue(schemaMatch);

        String parseJson =
                """
                        {
                            "id": 1,
                            "age": 30,
                            "token": "abc123",
                            "balance": 100,
                            "active": true,
                            "config": {
                                "timestamp": 1234567890,
                                "latitude": 1.0,
                                "longitude": 1.0,
                                "shortValue": 1,
                                "byteValue": 1,
                                "charValue": "a"
                            },
                            "roles": [{
                                    "value": 1
                                },
                                {
                                    "value": 2
                                },
                                {
                                    "value": 3
                                }
                            ],
                            "buddy": [{
                                    "id": "john.ivy@example.com",
                                    "profile": {
                                        "name": "John",
                                        "age": 12
                                    }
                                },
                                {
                                    "id": "jane.li@company.com",
                                    "profile": {
                                        "name": "Jane",
                                        "age": 25
                                    }
                                }
                            ],
                            "permissions": [
                                "read",
                                "write"
                            ]
                        } """;
        ISchemaValue schemaValue =
                SchemaOperator.evaluateValue(schema, schemaPath, getFactory(), parseJson);
        Object valueSerializableFormat = schemaValue.toSerializableFormat();

        String expectedJson =
                """
                        {
                        	"charValue": "a",
                        	"latitude": 1.0,
                        	"roles": [1, 2, 3],
                        	"active": true,
                        	"friends": [{
                        		"name": "John",
                        		"age": 12,
                        		"email": "john.ivy@example.com"
                        	}, {
                        		"name": "Jane",
                        		"age": 18,
                        		"email": "jane.li@company.com"
                        	}],
                        	"token": "abc123",
                        	"balance": 100,
                        	"permissions": ["read", "write"],
                        	"shortValue": 1,
                        	"id": 2,
                        	"age": 18,
                        	"timestamp": 1234567890,
                        	"longitude": 1.0,
                        	"byteValue": 1
                        }
                        """;
        Assertions.assertEquals(
                getFactory().create().writeTree(getFactory().create().readTree(expectedJson)),
                getFactory().create().writeValueAsString(valueSerializableFormat));
    }
}
