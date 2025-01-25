/* (C)2025 */
package com.roiocam.jsm.fastjson;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.facade.JSONToolsFactory;

public class FastjsonToolsFactory implements JSONToolsFactory {
    @Override
    public JSONTools create() {
        return new FastjsonTools();
    }
}
