/* (C)2025 */
package com.roiocam.jsm.tools;

import static org.junit.jupiter.api.Assertions.*;

import com.roiocam.jsm.schema.SchemaExample;
import com.roiocam.jsm.schema.SchemaNode;

class SchemaGeneratorTest {
    // Example object
    class Profile {
        public String name;
        public int age;
    }

    class User {
        public String token;
        public Profile user;
    }

    public void test() {

        //        ObjectMapper objectMapper = new ObjectMapper();

        // Create example Java object
        Profile profile = new Profile();
        profile.name = "John";
        profile.age = 30;

        User user = new User();
        user.token = "abc123";
        user.user = profile;

        // Generate schema
        SchemaNode schema = SchemaGenerator.generateSchema(user);
        // Serialize schema to JSON
        System.out.println("Serialized schema.Schema:");
        Object serializableForm = schema.toSerializableFormat();
        //        String schemaJson =
        // objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serializableForm);
        //        System.out.println(schemaJson);

        // Generate example JSON
        System.out.println("Example JSON:");
        SchemaExample exampleJson = schema.generateExample();
        Object serializableForm1 = exampleJson.toSerializableFormat();
        //        String exampleJsonString =
        // objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serializableForm1);
        //        System.out.println(exampleJsonString);
    }
}
