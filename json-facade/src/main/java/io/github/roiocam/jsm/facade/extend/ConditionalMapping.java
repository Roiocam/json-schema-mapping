/* (C)2025 */
package io.github.roiocam.jsm.facade.extend;

import java.util.List;

public class ConditionalMapping {
    private final String jsonPath;
    private final List<Mapping> mappings;

    public ConditionalMapping(String jsonPath, List<Mapping> mappings) {
        this.jsonPath = jsonPath;
        this.mappings = mappings;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JsonPath: ").append(jsonPath).append("\n");
        for (Mapping m : mappings) {
            sb.append(m.toString()).append("\n");
        }
        return sb.toString();
    }
}
