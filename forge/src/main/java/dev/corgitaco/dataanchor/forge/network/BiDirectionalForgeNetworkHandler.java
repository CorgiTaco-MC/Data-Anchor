package dev.corgitaco.dataanchor.forge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.BiDirectionalNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.BiDirectionalPacketBroadcaster;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

@AutoService(BiDirectionalPacketBroadcaster.class)
public class BiDirectionalForgeNetworkHandler extends ForgeNetworkHandler implements BiDirectionalPacketBroadcaster {

    public BiDirectionalForgeNetworkHandler() {
        super(NetworkDirection.BIDIRECTIONAL);
    }

    @Override
    public void registerPackets() {
        BiDirectionalNetworkContainer.BI_NAMESPACED_CONTAINERS.forEach((s, networkContainer) -> networkContainer.registerMessages(this::registerMessage));
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        channels.get(packet.getClass()).send(packet, PacketDistributor.SERVER.noArg());
    }

    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        channels.get(msg.getClass()).send(msg, PacketDistributor.PLAYER.with(player));
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        channels.get(msg.getClass()).send(msg, PacketDistributor.ALL.noArg());
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ServerLevel dimension) {
        channels.get(msg.getClass()).send(msg, PacketDistributor.DIMENSION.with(dimension.dimension()));
    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ServerLevel dimension, double x, double y, double z, double radius) {
        channels.get(msg.getClass()).send(msg, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, radius, dimension.dimension())));
    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        channels.get(msg.getClass()).send(msg, PacketDistributor.TRACKING_ENTITY.with(entity));
    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        channels.get(msg.getClass()).send(msg, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        channels.get(msg.getClass()).send(msg, PacketDistributor.TRACKING_CHUNK.with(chunk));
    }
}
