/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.forge;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import dev.corgitaco.dataanchor.network.register.PacketRegister;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(DataAnchor.MOD_ID)
public class DataAnchorForge {
    public DataAnchorForge() {
        DataAnchor.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent fmlCommonSetupEvent) {
        PacketRegister.ALL.forEach(PacketRegister::registerPackets);
    }
}
