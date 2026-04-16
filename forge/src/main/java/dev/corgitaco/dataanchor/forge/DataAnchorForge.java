/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.forge;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.forge.registry.ForgeRegistryHelper;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(DataAnchor.MOD_ID)
public class DataAnchorForge {
    public DataAnchorForge(final FMLJavaModLoadingContext context) {
        DataAnchor.init();
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ForgeRegistryHelper.CACHED.values().forEach(deferredRegister -> deferredRegister.register(modEventBus));
        modEventBus.<DataPackRegistryEvent.NewRegistry>addListener(newRegistry -> ForgeRegistryHelper.DATAPACK_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(newRegistry)));
        ForgeRegistryHelper.CACHED.forEach((resourceKey, deferredRegister) -> deferredRegister.register(modEventBus));
    }

    public void commonSetup(final FMLCommonSetupEvent fmlCommonSetupEvent) {
        PacketBroadcaster.ALL.forEach(PacketBroadcaster::registerPackets);
    }
}
