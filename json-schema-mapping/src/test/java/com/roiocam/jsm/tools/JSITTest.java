/* (C)2025 */
package com.roiocam.jsm.tools;

import com.roiocam.jsm.facade.JSONTools;
import com.roiocam.jsm.jackson.JacksonToolsFactory;

public class JSITTest extends ITTest {

    @Override
    JSONTools createTools() {
        return new JacksonToolsFactory().create();
    }
}
