/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.fastjson.FastjsonToolsFactory;

public class FJSchemaParserTest extends SchemaParserTest {

    @Override
    JSONTools createTools() {
        return new FastjsonToolsFactory().create();
    }
}
