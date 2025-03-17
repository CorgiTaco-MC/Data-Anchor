package dev.corgitaco.dataanchor.neoforge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
import net.neoforged.neoforge.network.PacketDistributor;

@AutoService(C2SPacketBroadcaster.class)
public class C2SNeoForgePacketBroadcaster implements C2SPacketBroadcaster {

    @Override
    public <MSG extends Packet> void sendToServer(MSG msg) {
        PacketDistributor.sendToServer(msg);
    }

    @Override
    public void registerPackets() {
        // Empty, we use NeoForge's packet registration event instead.
    }
}
