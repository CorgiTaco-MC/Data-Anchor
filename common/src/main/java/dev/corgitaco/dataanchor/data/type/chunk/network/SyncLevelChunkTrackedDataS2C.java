/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.chunk.network;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.chunk.SyncedLevelChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.level.network.SyncLevelTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public record SyncLevelChunkTrackedDataS2C(TrackedDataKey<SyncedLevelChunkTrackedData> dataKey, ChunkPos pos,
                                           CompoundTag tag) implements Packet {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLevelChunkTrackedDataS2C> STREAM_CODEC = CustomPacketPayload.codec(SyncLevelChunkTrackedDataS2C::write, SyncLevelChunkTrackedDataS2C::new);
    public static final CustomPacketPayload.Type<SyncLevelChunkTrackedDataS2C> TYPE = new CustomPacketPayload.Type<>(DataAnchor.id("chunk_tracked_data"));
    public SyncLevelChunkTrackedDataS2C(FriendlyByteBuf buf) {
        this((TrackedDataKey) TrackedDataKey.fromID(TrackedDataRegistries.CHUNK, buf.readResourceLocation()), new ChunkPos(buf.readInt(), buf.readInt()), buf.readNbt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dataKey.getId());
        buf.writeInt(pos.x);
        buf.writeInt(pos.z);
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        if (!chunk.isEmpty()) {
            if (chunk instanceof TrackedDataContainer access) {
                access.dataAnchor$getTrackedData(this.dataKey).ifPresent(data -> {
                    if (data instanceof SyncedLevelChunkTrackedData trackedData) {
                        trackedData.readFromNetwork(tag);
                    }
                });
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
