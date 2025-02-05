/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.jackson.JacksonFactory;

public class JSITTest extends ITTest {
    private static final JacksonFactory factory = new JacksonFactory();

    @Override
    JSONFactory getFactory() {
        return factory;
    }
}
