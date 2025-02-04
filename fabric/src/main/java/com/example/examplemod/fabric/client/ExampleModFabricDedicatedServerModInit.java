package com.example.examplemod.fabric.client;

import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ExampleModFabricDedicatedServerModInit implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        S2CPacketBroadcaster.INSTANCE.registerPackets();
    }
}
