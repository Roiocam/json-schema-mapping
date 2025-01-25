/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.fastjson.FastjsonToolsFactory;

public class FJSchemaOperatorTest extends SchemaOperatorTest {
    @Override
    JSONTools createTools() {
        return new FastjsonToolsFactory().create();
    }
}
