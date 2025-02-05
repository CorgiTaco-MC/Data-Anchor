package com.example.examplemod.forge;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.broadcast.BiDirectionalPacketBroadcaster;
import com.example.examplemod.network.broadcast.C2SPacketBroadcaster;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(ExampleMod.MOD_ID)
public class ExampleModForge {
    public ExampleModForge() {
        ExampleMod.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent fmlCommonSetupEvent) {
        S2CPacketBroadcaster.INSTANCE.registerPackets();
        C2SPacketBroadcaster.INSTANCE.registerPackets();
        BiDirectionalPacketBroadcaster.INSTANCE.registerPackets();
    }
}
