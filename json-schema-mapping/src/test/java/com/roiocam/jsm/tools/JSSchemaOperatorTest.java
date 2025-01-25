/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.jackson.JacksonToolsFactory;

public class JSSchemaOperatorTest extends SchemaOperatorTest {
    @Override
    JSONTools createTools() {
        return new JacksonToolsFactory().create();
    }
}
