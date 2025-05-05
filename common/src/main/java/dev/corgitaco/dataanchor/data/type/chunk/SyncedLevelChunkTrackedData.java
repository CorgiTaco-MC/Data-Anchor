/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.chunk.network.SyncLevelChunkTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract non-sealed class SyncedLevelChunkTrackedData extends LevelChunkTrackedData implements SyncedTrackedData {

    public SyncedLevelChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, LevelChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public void sync() {
        Level level = get().getLevel();
        if (!level.isClientSide) {
            S2CPacketBroadcaster.S2C.trackingChunk(syncPacket(), get());
        }
    }

    @Override
    public void syncToPlayer(ServerPlayer player) {
        S2CPacketBroadcaster.S2C.sendToPlayer(syncPacket(), player);
    }

    @Override
    public Packet syncPacket() {
        return new SyncLevelChunkTrackedDataS2C((TrackedDataKey<SyncedLevelChunkTrackedData>) trackedDataKey, chunk.getPos(), writeToNetwork());
    }
}
