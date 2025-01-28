/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONFactory;
import com.roiocam.jsm.fastjson.FastjsonFactory;

public class FJSchemaOperatorTest extends SchemaOperatorTest {
    private static final JSONFactory factory = new FastjsonFactory();

    @Override
    JSONFactory getFactory() {
        return factory;
    }
}
