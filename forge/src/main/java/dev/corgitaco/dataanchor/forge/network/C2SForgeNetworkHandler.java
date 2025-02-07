package dev.corgitaco.dataanchor.forge.network;

import dev.corgitaco.dataanchor.network.C2SNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
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
