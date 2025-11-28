/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.network;

import dev.corgitaco.dataanchor.network.broadcast.BiDirectionalPacketBroadcaster;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.Map;

public class BiDirectionalNetworkContainer extends NetworkContainer implements BiDirectionalPacketBroadcaster {
    public static final Map<String, BiDirectionalNetworkContainer> BI_NAMESPACED_CONTAINERS = new HashMap<>();

    public BiDirectionalNetworkContainer(String namespace) {
        super(namespace);
    }

    public static BiDirectionalNetworkContainer of(String namespace) {
        BiDirectionalNetworkContainer networkContainer = BI_NAMESPACED_CONTAINERS.get(namespace);
        if (networkContainer != null) {
            return networkContainer;
        }

        BiDirectionalNetworkContainer networkContainer1 = new BiDirectionalNetworkContainer(namespace);
        BI_NAMESPACED_CONTAINERS.put(namespace, networkContainer1);
        return networkContainer1;
    }

    @Override
    public <MSG extends Packet> void sendToServer(MSG msg) {
        PacketBroadcaster.BI.sendToServer(msg);
    }

    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        PacketBroadcaster.BI.sendToPlayer(msg, player);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        PacketBroadcaster.BI.sendToAllPlayers(msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ResourceKey<Level> dimensionKey) {
        PacketBroadcaster.BI.sendToAllPlayersInDimension(msg, dimensionKey);
    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, double x, double y, double z, double radius) {
        PacketBroadcaster.BI.sendNearPositionInDimension(msg, dimensionKey, x, y, z, radius);
    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        PacketBroadcaster.BI.trackingEntity(msg, entity);
    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        PacketBroadcaster.BI.trackingEntityAndSelf(msg, entity);
    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        PacketBroadcaster.BI.trackingChunk(msg, chunk);
    }

    @Override
    public ResourceLocation channelName(Class<? extends Packet> packetClass) {
        return PacketBroadcaster.BI.channelName(packetClass);
    }
}
