/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.network.broadcast;

import dev.corgitaco.dataanchor.network.Packet;
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
