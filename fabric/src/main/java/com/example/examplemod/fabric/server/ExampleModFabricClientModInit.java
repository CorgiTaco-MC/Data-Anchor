package com.example.examplemod.fabric.server;

import com.example.examplemod.network.PacketBroadcaster;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ExampleModFabricClientModInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PacketBroadcaster.INSTANCE.registerPackets();
    }
}
