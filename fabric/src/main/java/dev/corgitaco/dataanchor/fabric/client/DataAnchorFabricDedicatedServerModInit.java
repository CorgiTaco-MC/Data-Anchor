package dev.corgitaco.dataanchor.fabric.client;

import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.fabricmc.api.DedicatedServerModInitializer;

public class DataAnchorFabricDedicatedServerModInit implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        PacketBroadcaster.ALL.forEach(PacketBroadcaster::registerPackets);
    }
}
