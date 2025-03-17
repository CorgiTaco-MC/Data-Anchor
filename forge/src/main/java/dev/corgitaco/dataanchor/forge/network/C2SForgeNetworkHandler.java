package dev.corgitaco.dataanchor.forge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.C2SNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
import net.minecraftforge.network.PacketDistributor;

@AutoService(C2SPacketBroadcaster.class)
public class C2SForgeNetworkHandler extends ForgeNetworkHandler implements C2SPacketBroadcaster {

    public C2SForgeNetworkHandler() {
        super(NetworkDirection.C2S);
    }

    @Override
    public void registerPackets() {
        C2SNetworkContainer.C2S_NAMESPACED_CONTAINERS.forEach((s, networkContainer) -> networkContainer.registerMessages(this::registerMessage));
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        channels.get(packet.getClass()).send(packet, PacketDistributor.SERVER.noArg());
    }
}
