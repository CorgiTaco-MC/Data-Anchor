package com.example.examplemod.fabric.client;

import com.example.examplemod.network.broadcast.BiDirectionalPacketBroadcaster;
import com.example.examplemod.network.broadcast.C2SPacketBroadcaster;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ExampleModFabricDedicatedServerModInit implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        S2CPacketBroadcaster.INSTANCE.registerPackets();
        C2SPacketBroadcaster.INSTANCE.registerPackets();
        BiDirectionalPacketBroadcaster.INSTANCE.registerPackets();
    }
}
