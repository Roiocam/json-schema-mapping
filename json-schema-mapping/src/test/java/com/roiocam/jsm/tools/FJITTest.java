/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.fastjson.FastjsonToolsFactory;

public class FJITTest extends ITTest {

    @Override
    JSONTools createTools() {
        return new FastjsonToolsFactory().create();
    }
}
