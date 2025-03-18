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

public class C2SNetworkContainer extends NetworkContainer {
    public static final Map<String, C2SNetworkContainer> C2S_NAMESPACED_CONTAINERS = new HashMap<>();

    public C2SNetworkContainer(String namespace) {
        super(namespace);
    }
    public static C2SNetworkContainer of(String namespace) {
        C2SNetworkContainer networkContainer = C2S_NAMESPACED_CONTAINERS.get(namespace);
        if (networkContainer != null) {
            return networkContainer;
        }

        C2SNetworkContainer networkContainer1 = new C2SNetworkContainer(namespace);
        C2S_NAMESPACED_CONTAINERS.put(namespace, networkContainer1);
        return networkContainer1;
    }

}
