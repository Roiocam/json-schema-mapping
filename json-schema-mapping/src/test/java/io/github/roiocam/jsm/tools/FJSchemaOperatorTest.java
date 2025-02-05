/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.fastjson.FastjsonFactory;

public class FJSchemaOperatorTest extends SchemaOperatorTest {
    private static final JSONFactory factory = new FastjsonFactory();

    @Override
    JSONFactory getFactory() {
        return factory;
    }
}
