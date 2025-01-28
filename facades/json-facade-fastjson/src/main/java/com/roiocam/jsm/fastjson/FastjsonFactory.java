/* (C)2025 */
package com.roiocam.jsm.fastjson;

import com.roiocam.jsm.facade.JSONFactory;
import com.roiocam.jsm.facade.JSONPathContext;
import com.roiocam.jsm.facade.JSONTools;

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
