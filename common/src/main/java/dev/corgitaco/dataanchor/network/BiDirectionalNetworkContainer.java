/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.network;

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
