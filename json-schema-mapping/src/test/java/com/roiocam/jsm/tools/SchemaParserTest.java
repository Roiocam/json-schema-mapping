/* (C)2025 */
package com.roiocam.jsm.tools;

import static org.junit.jupiter.api.Assertions.*;

import com.roiocam.jsm.json.JSONTools;
import com.roiocam.jsm.schema.SchemaExample;
import com.roiocam.jsm.schema.SchemaNode;
import org.junit.jupiter.api.Test;

class SchemaParserTest {

    @Test
    public void test() {
        String schemaJson =
                """
        {
          "token": "string",
          "user": {
            "name": "string",
            "age": "int"
          }
        }
        """;

        // Parse the schema JSON into a schema.SchemaNode
        JSONTools tools = null;
        SchemaNode schemaNode = SchemaParser.parse(tools, schemaJson);

        // Print the parsed schema.SchemaNode
        System.out.println("Parsed schema.SchemaNode:");
        System.out.println(schemaNode);

        // Generate Example JSON from schema.SchemaNode
        SchemaExample exampleJson = schemaNode.generateExample();

        // Serialize Example JSON
        //        ObjectMapper objectMapper = new ObjectMapper();
        //        Object serializableForm = exampleJson.toSerializableFormat();
        //        String exampleJsonString =
        // objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serializableForm);
        //        System.out.println("Example JSON:");
        //        System.out.println(exampleJsonString);
    }
}
