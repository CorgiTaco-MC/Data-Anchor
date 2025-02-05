package com.example.examplemod.network;

import java.util.HashMap;
import java.util.Map;

public class BiDirectionalNetworkContainer extends NetworkContainer {
    public static final Map<String, BiDirectionalNetworkContainer> BI_NAMESPACED_CONTAINERS = new HashMap<>();

    public BiDirectionalNetworkContainer(String namespace) {
        super(namespace);
    }

    public static BiDirectionalNetworkContainer of(String namespace) {
        BiDirectionalNetworkContainer networkContainer = BI_NAMESPACED_CONTAINERS.get(namespace);
        if (networkContainer != null) {
            return networkContainer;
        }

        BiDirectionalNetworkContainer networkContainer1 = new BiDirectionalNetworkContainer(namespace);
        BI_NAMESPACED_CONTAINERS.put(namespace, networkContainer1);
        return networkContainer1;
    }
}
