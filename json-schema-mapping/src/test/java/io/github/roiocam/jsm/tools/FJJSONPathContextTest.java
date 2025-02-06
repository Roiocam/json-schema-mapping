/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.fastjson.FastjsonFactory;

public class FJJSONPathContextTest extends JSONPathContextTest {

    @Override
    JSONPathContext getContext(String data) {
        return new FastjsonFactory().createPathContext(data);
    }
}
