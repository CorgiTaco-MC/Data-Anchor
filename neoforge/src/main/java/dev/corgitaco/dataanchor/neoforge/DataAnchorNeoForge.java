/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.neoforge;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.neoforge.registry.NeoforgeRegistryHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

/**
 * Main class for the mod on the NeoForge platform.
 */
@Mod(DataAnchor.MOD_ID)
public class DataAnchorNeoForge {
    public DataAnchorNeoForge(IEventBus eventBus) {
        DataAnchor.init();

        eventBus.addListener(DataPackRegistryEvent.NewRegistry.class, newRegistry -> NeoforgeRegistryHelper.DATAPACK_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(newRegistry)));
        eventBus.addListener(NewRegistryEvent.class, newRegistry -> NeoforgeRegistryHelper.NEW_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(newRegistry)));
        NeoforgeRegistryHelper.CACHED.forEach((resourceKey, deferredRegister) -> deferredRegister.register(eventBus));
    }
}
