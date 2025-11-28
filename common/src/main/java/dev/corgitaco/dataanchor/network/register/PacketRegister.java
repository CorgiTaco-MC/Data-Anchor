package dev.corgitaco.dataanchor.network.register;

import dev.corgitaco.dataanchor.util.ServiceUtil;

import java.util.List;

public interface PacketRegister {

    S2CPacketRegister S2C = ServiceUtil.load(S2CPacketRegister.class);
    C2SPacketRegister C2S = ServiceUtil.load(C2SPacketRegister.class);
    BidirectionalPacketRegister BI = ServiceUtil.load(BidirectionalPacketRegister.class);

    List<PacketRegister> ALL = List.of(S2C, C2S, BI);


    void registerPackets();
}
