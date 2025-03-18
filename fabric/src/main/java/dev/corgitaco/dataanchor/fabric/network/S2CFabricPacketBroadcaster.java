/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.fabric.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.fabric.DataAnchorFabric;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;

@AutoService(S2CPacketBroadcaster.class)
public class S2CFabricPacketBroadcaster extends FabricPacketBroadcaster implements S2CPacketBroadcaster {

    @Override
    public void registerPackets() {
        S2CNetworkContainer.S2C_NAMESPACED_CONTAINERS.forEach((modId, networkContainer) -> networkContainer.registerMessages(this::register));
    }

    @Override
    protected <T extends Packet> void registerPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> serializer) {
        PayloadTypeRegistry.playS2C().register(type, serializer);
    }

    @Override
    protected <T extends Packet> void registerHandler(Packet.Handler<T> handler) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientProxy.registerClientReceiver(handler);
        }
    }

    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        ServerPlayNetworking.send(player, msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        MinecraftServer server = DataAnchorFabric.server;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendToPlayer(msg, player);
        }
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ServerLevel dimension) {
        for (ServerPlayer player : dimension.players()) {
            sendToPlayer(msg, player);
        }
    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ServerLevel dimension, double x, double y, double z, double radius) {
        for (ServerPlayer player : dimension.players()) {
            if (player.distanceToSqr(x, y, z) <= Mth.square(radius)) {
                sendToPlayer(msg, player);
            }
        }
    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        ServerLevel level = (ServerLevel) entity.level();
        ChunkMap.TrackedEntity trackedEntity = level.getChunkSource().chunkMap.entityMap.get(entity.getId());

        if (trackedEntity != null) {
            for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
                ServerPlayer player = serverPlayerConnection.getPlayer();
                sendToPlayer(msg, player);
            }
        }
    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        ServerLevel level = (ServerLevel) entity.level();
        ChunkMap.TrackedEntity trackedEntity = level.getChunkSource().chunkMap.entityMap.get(entity.getId());

        if (trackedEntity != null) {
            for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
                ServerPlayer player = serverPlayerConnection.getPlayer();
                sendToPlayer(msg, player);
            }
            if (trackedEntity.entity instanceof ServerPlayer serverPlayer) {
                sendToPlayer(msg, serverPlayer);
            }
        }
    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        ServerLevel level = (ServerLevel) chunk.getLevel();
        ChunkMap chunkMap = level.getChunkSource().chunkMap;

        chunkMap.getPlayers(chunk.getPos(), false).forEach(serverPlayer -> sendToPlayer(msg, serverPlayer));
    }
}
