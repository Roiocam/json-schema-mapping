/* (C)2025 */
package com.roiocam.jsm.facade;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

public class JSONToolsFactories {

    private static final Set<JSONToolsFactory> FACTORIES = new TreeSet<>();

    static {
        ServiceLoader.load(JSONToolsFactory.class).forEach(FACTORIES::add);
    }

    public static JSONTools create() {
        return FACTORIES.stream().findFirst().get().create();
    }

    public static void register(JSONToolsFactory factory) {
        FACTORIES.add(factory);
    }
}
