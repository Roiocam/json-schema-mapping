/* (C)2025 */
package io.github.roiocam.jsm.facade.extend;

public class DefaultMapping extends Mapping {

    public DefaultMapping(String result) {
        super(null, result);
    }

    @Override
    public String toString() {
        return "Default -> " + getResult();
    }
}
