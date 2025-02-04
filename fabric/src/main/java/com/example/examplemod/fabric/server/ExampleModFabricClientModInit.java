package com.example.examplemod.fabric.server;

import com.example.examplemod.network.broadcast.C2SPacketBroadcaster;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModFabricClientModInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        S2CPacketBroadcaster.INSTANCE.registerPackets();
        C2SPacketBroadcaster.INSTANCE.registerPackets();
    }
}
