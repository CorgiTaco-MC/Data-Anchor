package dev.corgitaco.dataanchor.forge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

@AutoService(S2CPacketBroadcaster.class)
public class S2CForgeNetworkHandler extends ForgeNetworkHandler implements S2CPacketBroadcaster {

    @Override
    public void registerPackets() {
        S2CNetworkContainer.S2C_NAMESPACED_CONTAINERS.forEach((s, networkContainer) -> networkContainer.registerMessages(this::registerMessage));
    }

    @Override
    public ResourceLocation channelName(Class<? extends Packet> packetClass) {
        return this.channelNames.get(packetClass);
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
