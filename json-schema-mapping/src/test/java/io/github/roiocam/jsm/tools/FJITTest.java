/* (C)2025 */
package io.github.roiocam.jsm.tools;

import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.fastjson.FastjsonFactory;

public class FJITTest extends ITTest {
    private static final JSONFactory factory = new FastjsonFactory();

    @Override
    JSONFactory getFactory() {
        return factory;
    }
}
