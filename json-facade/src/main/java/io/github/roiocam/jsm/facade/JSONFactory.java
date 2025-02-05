/* (C)2025 */
package io.github.roiocam.jsm.facade;

public interface JSONFactory {

    JSONTools create();

    JSONPathContext createPathContext(String json);
}
