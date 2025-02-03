package com.example.examplemod.fabric.client;

import com.example.examplemod.network.PacketBroadcaster;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ExampleModFabricDedicatedServerModInit implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        PacketBroadcaster.INSTANCE.registerPackets();
    }
}
