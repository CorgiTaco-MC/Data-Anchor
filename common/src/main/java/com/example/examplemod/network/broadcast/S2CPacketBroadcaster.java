package com.example.examplemod.network.broadcast;

import com.example.examplemod.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public interface S2CPacketBroadcaster extends PacketBroadcaster {
    S2CPacketBroadcaster INSTANCE = S2C;

    <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player);

    <MSG extends Packet> void sendToAllPlayers(MSG msg);

    default <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ServerPlayer player) {
        sendToAllPlayersInDimension(msg, player.serverLevel().dimension());
    }

    <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ResourceKey<Level> dimensionKey);

    default <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, BlockPos position, double radius) {
        sendNearPositionInDimension(msg, dimensionKey, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, radius);
    }

    default <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, Position position, double radius) {
        sendNearPositionInDimension(msg, dimensionKey, position.x(), position.y(), position.z(), radius);
    }

    <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, double x, double y, double z, double radius);

    <MSG extends Packet> void trackingEntity(MSG msg, Entity entity);

    <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity);

    <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk);
}
