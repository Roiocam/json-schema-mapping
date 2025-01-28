/* (C)2025 */
package com.roiocam.jsm.facade;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

public class JSONFactories {

    private static final Set<JSONFactory> FACTORIES = new TreeSet<>();

    static {
        ServiceLoader.load(JSONFactory.class).forEach(FACTORIES::add);
    }

    public static JSONTools create() {
        return FACTORIES.stream().findFirst().get().create();
    }

    public static JSONPathContext createPathContext(String json) {
        return FACTORIES.stream().findFirst().get().createPathContext(json);
    }

    public static void register(JSONFactory factory) {
        FACTORIES.add(factory);
    }
}
