package dev.corgitaco.dataanchor.forge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.BiDirectionalNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.BiDirectionalPacketBroadcaster;
import dev.corgitaco.dataanchor.network.register.BidirectionalPacketRegister;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

@AutoService({BiDirectionalPacketBroadcaster.class, BidirectionalPacketRegister.class})
public class BiDirectionalForgeNetworkHandler extends ForgeNetworkHandler implements BiDirectionalPacketBroadcaster, BidirectionalPacketRegister {

    public BiDirectionalForgeNetworkHandler() {
    }

    @Override
    public void registerPackets() {
        BiDirectionalNetworkContainer.BI_NAMESPACED_CONTAINERS.forEach((s, networkContainer) -> networkContainer.registerMessages(this::registerMessage));
    }

    @Override
    public ResourceLocation channelName(Class<? extends Packet> packetClass) {
        return this.channelNames.get(packetClass);
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        channels.get(packet.getClass()).sendToServer(packet);
    }

    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        channels.get(msg.getClass()).send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        channels.get(msg.getClass()).send(PacketDistributor.ALL.noArg(), msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ResourceKey<Level> dimensionKey) {
        channels.get(msg.getClass()).send(PacketDistributor.DIMENSION.with(() -> dimensionKey), msg);
    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, double x, double y, double z, double radius) {
        channels.get(msg.getClass()).send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, dimensionKey)), msg);
    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        channels.get(msg.getClass()).send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        channels.get(msg.getClass()).send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        channels.get(msg.getClass()).send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
    }
}
