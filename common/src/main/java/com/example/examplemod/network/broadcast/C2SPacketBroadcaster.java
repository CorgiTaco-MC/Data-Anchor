package com.example.examplemod.network.broadcast;

import com.example.examplemod.network.Packet;

public interface C2SPacketBroadcaster extends PacketBroadcaster {
    C2SPacketBroadcaster INSTANCE = C2S;

    <MSG extends Packet> void sendToServer(MSG msg);
}
