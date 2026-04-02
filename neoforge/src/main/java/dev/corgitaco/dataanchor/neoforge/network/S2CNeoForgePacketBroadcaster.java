/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.neoforge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

@AutoService(S2CPacketBroadcaster.class)
public class S2CNeoForgePacketBroadcaster implements S2CPacketBroadcaster {
    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        ClientPacketDistributor.sendToServer(msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ServerLevel dimension) {
        PacketDistributor.sendToPlayersInDimension(dimension, msg);
    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ServerLevel dimension, double x, double y, double z, double radius) {
        PacketDistributor.sendToPlayersNear(dimension, null, x, y, z, radius, msg);
    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, msg);
    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, msg);
    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) chunk.getLevel(), chunk.getPos(), msg);
    }

    @Override
    public void registerPackets() {
        // Empty, we use NeoForge's packet registration event instead.
    }
}
