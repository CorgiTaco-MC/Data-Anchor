package com.example.examplemod.forge.network;

import com.example.examplemod.network.C2SNetworkContainer;
import com.example.examplemod.network.Packet;
import com.example.examplemod.network.S2CNetworkContainer;
import com.example.examplemod.network.broadcast.C2SPacketBroadcaster;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import com.google.auto.service.AutoService;

@AutoService(C2SPacketBroadcaster.class)
public class C2SForgeNetworkHandler extends ForgeNetworkHandler implements C2SPacketBroadcaster {

    @Override
    public void registerPackets() {
        C2SNetworkContainer.C2S_NAMESPACED_CONTAINERS.forEach((s, networkContainer) -> networkContainer.registerMessages(this::registerMessage));
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        channels.get(packet.getClass()).sendToServer(packet);
    }
}
