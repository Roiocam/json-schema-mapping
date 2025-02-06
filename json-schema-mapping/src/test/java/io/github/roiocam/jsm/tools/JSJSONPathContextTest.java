/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.jackson.JacksonFactory;

public class JSJSONPathContextTest extends JSONPathContextTest {
    @Override
    JSONPathContext getContext(String data) {
        return new JacksonFactory().createPathContext(data);
    }
}
