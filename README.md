# JSON-Schema-Mappings

A simple tool for mapping JSON with different structures based on JSON Path.

```json
---------------------------------------------------------------------------------------------------------
Schema:                                                  |  Schema Example:
{                                                        |  {
    "type" : "com.roiocam.jsm.tools.User",               |      "user" : {                                               
    "user" : {                                           |          "name" : "$.",
        "name" : "string",                               |          "age" : "$.",                
        "age" : "int",                                   |          "email" : "$."            
        "email" : "string"                               |      },                
    },                                                   |      "token" : "$."
    "token" : "string"                                   |  }            
}                                                        |
---------------------------------------------------------------------------------------------------------
Difference Structure:                                    |  Mapping Path:             
{                                                        |  {
  "username" : "John",                                   |      "user" : {                     
  "profile" : {                                          |          "name" : "$.username",              
    "age" : 30,                                          |          "age" : "$.profile.age",              
    "email" : "jacky.chen@example.com"                   |          "email" : "$.profile.email"                                     
  },                                                     |      },   
  "token" : "123456"                                     |      "token" : "$.token"                   
}                                                        |  }
---------------------------------------------------------------------------------------------------------
Parsed Value:                                            |  Parsed Object:            
{                                                        |  {
    "user" : {                                           |      "token" : "123456",             
        "name" : "John",                                 |      "user" : {                       
        "age" : 30,                                      |          "name" : "John",                 
        "email" : "jacky.chen@example.com"               |          "age" : 30,                                         
    },                                                   |          "email" : "jacky.chen@example.com"     
    "token" : "123456"                                   |      }                     
}                                                        |  }
---------------------------------------------------------------------------------------------------------

```