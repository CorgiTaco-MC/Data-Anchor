package com.example.examplemod.forge;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.PacketBroadcaster;
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
        PacketBroadcaster.INSTANCE.registerPackets();
    }
}
