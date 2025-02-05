/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.gson.GsonFactory;

public class GSSchemaOperatorTest extends SchemaOperatorTest {
    private static final JSONFactory factory = new GsonFactory();

    @Override
    JSONFactory getFactory() {
        return factory;
    }
}
