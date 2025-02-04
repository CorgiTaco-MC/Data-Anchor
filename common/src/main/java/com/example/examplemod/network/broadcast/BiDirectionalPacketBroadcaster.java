package com.example.examplemod.network.broadcast;

import com.example.examplemod.util.ServiceUtil;

public interface BiDirectionalPacketBroadcaster extends S2CPacketBroadcaster, C2SPacketBroadcaster {

    BiDirectionalPacketBroadcaster INSTANCE = ServiceUtil.load(BiDirectionalPacketBroadcaster.class);



}
