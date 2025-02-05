package com.example.examplemod.fabric.client;

import com.example.examplemod.network.broadcast.BiDirectionalPacketBroadcaster;
import com.example.examplemod.network.broadcast.C2SPacketBroadcaster;
import com.example.examplemod.network.broadcast.PacketBroadcaster;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ExampleModFabricDedicatedServerModInit implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        PacketBroadcaster.ALL.forEach(PacketBroadcaster::registerPackets);
    }
}
