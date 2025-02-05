# JSON-Schema-Mappings

A simple tool for mapping JSON with different structures based on JSON Path.

Support both structured and flatten schema.

```
---------------------------------------------------------------------------------------------------------
Schema:                                                  |  Schema Example:
{                                                        |  {
    "type" : "io.github.roiocam.jsm.tools.User",               |      "user" : {                                               
    "user" : {                                           |          "name" : "$.",
        "name" : "string",                               |          "age" : "$.",                
        "age" : "int",                                   |          "email" : "$."            
        "email" : "string"                               |      },                
    },                                                   |      "token" : "$."
    "token" : "string"                                   |  }            
}                                                        |
---------------------------------------------------------------------------------------------------------
Example Value:                                           |  Schema Path:             
{                                                        |  {
  "username" : "John",                                   |      "user" : {                     
  "profile" : {                                          |          "name" : "$.username",              
    "age" : 30,                                          |          "age" : "$.profile.age",              
    "email" : "jacky.chen@example.com"                   |          "email" : "$.profile.email"                                     
  },                                                     |      },   
  "token" : "123456"                                     |      "token" : "$.token"                   
}                                                        |  }
---------------------------------------------------------------------------------------------------------
Schema Value:                                            |  Parsed Object:            
{                                                        |  {
    "user" : {                                           |      "token" : "123456",             
        "name" : "John",                                 |      "user" : {                       
        "age" : 30,                                      |          "name" : "John",                 
        "email" : "jacky.chen@example.com"               |          "age" : 30,                                         
    },                                                   |          "email" : "jacky.chen@example.com"     
    "token" : "123456"                                   |      }                     
}                                                        |  }
---------------------------------------------------------------------------------------------------------
Flatten Key:                                             |  Flatten Key Path:           
{                                                        |  {
    "user.age" : "int",                                  |      "user.age" : "$.profile.age",                      
    "user.name" : "string",                              |      "user.name" : "$.username",                          
    "user.email" : "string",                             |      "user.email" : "$.profile.email",                           
    "token" : "string"                                   |      "token" : "$.token"                     
}                                                        |  }
---------------------------------------------------------------------------------------------------------
```