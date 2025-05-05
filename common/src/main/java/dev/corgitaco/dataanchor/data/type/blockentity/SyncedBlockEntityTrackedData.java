/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.blockentity;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.blockentity.network.SyncBlockEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract non-sealed class SyncedBlockEntityTrackedData extends BlockEntityTrackedData implements SyncedTrackedData {
    public SyncedBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
        super(trackedDataKey, blockEntity);
    }

    @Override
    public void sync() {
        if (!blockEntity.getLevel().isClientSide) {
            PacketBroadcaster.S2C.trackingChunk(syncPacket(), blockEntity.getLevel().getChunkAt(blockEntity.getBlockPos()));
        }
    }

    @Override
    public void syncToPlayer(ServerPlayer player) {
        PacketBroadcaster.S2C.sendToPlayer(syncPacket(), player);
    }

    @Override
    public Packet syncPacket() {
        return new SyncBlockEntityTrackedDataS2C(blockEntity.getBlockPos(), trackedDataKey, writeToNetwork());
    }
}
