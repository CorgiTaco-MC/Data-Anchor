package com.example.examplemod.fabric;

import com.example.examplemod.ExampleMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

/**
 * This class is the entrypoint for the mod on the Fabric platform.
 */
public class ExampleModFabric implements ModInitializer {

    public static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        ExampleMod.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> ExampleModFabric.server = server);
    }
}
