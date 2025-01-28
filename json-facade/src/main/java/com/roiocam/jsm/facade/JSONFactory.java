/* (C)2025 */
package com.roiocam.jsm.facade;

public interface JSONFactory {

    JSONTools create();

    JSONPathContext createPathContext(String json);
}
