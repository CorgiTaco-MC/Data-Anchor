/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.network;

import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.Map;

public class S2CNetworkContainer extends NetworkContainer implements S2CPacketBroadcaster {
    public static final Map<String, S2CNetworkContainer> S2C_NAMESPACED_CONTAINERS = new HashMap<>();

    public S2CNetworkContainer(String namespace) {
        super(namespace);
    }

    public static S2CNetworkContainer of(String namespace) {
        S2CNetworkContainer networkContainer = S2C_NAMESPACED_CONTAINERS.get(namespace);
        if (networkContainer != null) {
            return networkContainer;
        }

        S2CNetworkContainer networkContainer1 = new S2CNetworkContainer(namespace);
        S2C_NAMESPACED_CONTAINERS.put(namespace, networkContainer1);
        return networkContainer1;
    }

    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        PacketBroadcaster.S2C.sendToPlayer(msg, player);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        PacketBroadcaster.S2C.sendToAllPlayers(msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ResourceKey<Level> dimensionKey) {
        PacketBroadcaster.S2C.sendToAllPlayersInDimension(msg, dimensionKey);
    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, double x, double y, double z, double radius) {
        PacketBroadcaster.S2C.sendNearPositionInDimension(msg, dimensionKey, x, y, z, radius);
    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        PacketBroadcaster.S2C.trackingEntity(msg, entity);
    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        PacketBroadcaster.S2C.trackingEntityAndSelf(msg, entity);
    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        PacketBroadcaster.S2C.trackingChunk(msg, chunk);
    }

    @Override
    public ResourceLocation channelName(Class<? extends Packet> packetClass) {
        return PacketBroadcaster.S2C.channelName(packetClass);
    }
}
