package dev.corgitaco.dataanchor.network.broadcast;

import dev.corgitaco.dataanchor.network.Packet;

public interface C2SPacketBroadcaster extends PacketBroadcaster {
    C2SPacketBroadcaster INSTANCE = C2S;

    <MSG extends Packet> void sendToServer(MSG msg);
}
