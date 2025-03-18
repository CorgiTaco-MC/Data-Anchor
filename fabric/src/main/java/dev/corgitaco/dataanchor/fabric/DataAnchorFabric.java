/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.fabric;

import dev.corgitaco.dataanchor.DataAnchor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

/**
 * This class is the entrypoint for the mod on the Fabric platform.
 */
public class DataAnchorFabric implements ModInitializer {

    public static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        DataAnchor.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> DataAnchorFabric.server = server);
    }
}
