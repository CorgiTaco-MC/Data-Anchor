package com.example.examplemod.network;

import java.util.HashMap;
import java.util.Map;

public class S2CNetworkContainer extends NetworkContainer {
    public static final Map<String, S2CNetworkContainer> S2C_NAMESPACED_CONTAINERS = new HashMap<>();

    public S2CNetworkContainer(String namespace) {
        super(namespace);
    }
    public static S2CNetworkContainer of(String namespace) {
        S2CNetworkContainer networkContainer = S2C_NAMESPACED_CONTAINERS.get(namespace);
        if (networkContainer != null) {
            return networkContainer;
        }

        S2CNetworkContainer networkContainer1 = new S2CNetworkContainer(namespace);
        S2C_NAMESPACED_CONTAINERS.put(namespace, networkContainer1);
        return networkContainer1;
    }

}
