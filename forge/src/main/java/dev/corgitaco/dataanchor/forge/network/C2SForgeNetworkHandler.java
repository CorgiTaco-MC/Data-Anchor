package dev.corgitaco.dataanchor.forge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.C2SNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
import net.minecraft.resources.ResourceLocation;

@AutoService(C2SPacketBroadcaster.class)
public class C2SForgeNetworkHandler extends ForgeNetworkHandler implements C2SPacketBroadcaster {

    @Override
    public void registerPackets() {
        C2SNetworkContainer.C2S_NAMESPACED_CONTAINERS.forEach((s, networkContainer) -> networkContainer.registerMessages(this::registerMessage));
    }

    @Override
    public ResourceLocation channelName(Class<? extends Packet> packetClass) {
        return this.channelNames.get(packetClass);
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        channels.get(packet.getClass()).sendToServer(packet);
    }
}
