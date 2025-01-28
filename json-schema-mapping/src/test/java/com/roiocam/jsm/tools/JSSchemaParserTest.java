/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONFactory;
import com.roiocam.jsm.jackson.JacksonFactory;

public class JSSchemaParserTest extends SchemaParserTest {
    private static final JacksonFactory factory = new JacksonFactory();

    @Override
    JSONFactory getFactory() {
        return factory;
    }
}
