package dev.corgitaco.dataanchor.network.broadcast;

import dev.corgitaco.dataanchor.util.ServiceUtil;

import java.util.List;

public interface PacketBroadcaster {
    S2CPacketBroadcaster S2C = ServiceUtil.load(S2CPacketBroadcaster.class);
    C2SPacketBroadcaster C2S = ServiceUtil.load(C2SPacketBroadcaster.class);
    BiDirectionalPacketBroadcaster BI = ServiceUtil.load(BiDirectionalPacketBroadcaster.class);
    List<PacketBroadcaster> ALL = List.of(S2C, C2S, BI);


    void registerPackets();
}
