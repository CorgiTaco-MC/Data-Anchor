package dev.corgitaco.dataanchor.fabric.server;

import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.fabricmc.api.ClientModInitializer;

public class DataAnchorFabricClientModInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PacketBroadcaster.ALL.forEach(PacketBroadcaster::registerPackets);
    }
}
