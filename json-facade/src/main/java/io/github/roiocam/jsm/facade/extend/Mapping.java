/* (C)2025 */
package io.github.roiocam.jsm.facade.extend;

public class Mapping {
    private final String condition;
    private final String result;

    public Mapping(String condition, String result) {
        this.condition = condition;
        this.result = result;
    }

    public String getCondition() {
        return condition;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "If [" + condition + "] then [" + result + "]";
    }
}
