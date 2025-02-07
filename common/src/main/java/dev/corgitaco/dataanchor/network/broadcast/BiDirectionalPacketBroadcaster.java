package dev.corgitaco.dataanchor.network.broadcast;

public interface BiDirectionalPacketBroadcaster extends S2CPacketBroadcaster, C2SPacketBroadcaster {
    BiDirectionalPacketBroadcaster INSTANCE = BI;
}
