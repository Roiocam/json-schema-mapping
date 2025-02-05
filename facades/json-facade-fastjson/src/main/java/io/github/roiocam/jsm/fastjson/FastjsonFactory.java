/* (C)2025 */
package io.github.roiocam.jsm.fastjson;

import io.github.roiocam.jsm.facade.JSONFactory;
import io.github.roiocam.jsm.facade.JSONPathContext;
import io.github.roiocam.jsm.facade.JSONTools;

public class FastjsonFactory implements JSONFactory {
    @Override
    public JSONTools create() {
        return new FastjsonTools();
    }

    @Override
    public JSONPathContext createPathContext(String json) {
        return new FastjsonPathContext(json);
    }
}
