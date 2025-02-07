package com.example.examplemod.data.type.blockentity;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.type.blockentity.network.SyncBlockEntityTrackedDataS2C;
import com.example.examplemod.network.broadcast.PacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract non-sealed class SyncedBlockEntityTrackedData extends BlockEntityTrackedData implements SyncedTrackedData {
    public SyncedBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
        super(trackedDataKey, blockEntity);
    }

    @Override
    public void sync() {
        if (!blockEntity.getLevel().isClientSide) {
            PacketBroadcaster.S2C.trackingChunk(new SyncBlockEntityTrackedDataS2C(blockEntity.getBlockPos(), trackedDataKey, writeToNetwork()), blockEntity.getLevel().getChunkAt(blockEntity.getBlockPos()));
        }
    }

    @Override
    public void syncToPlayer(ServerPlayer player) {
        PacketBroadcaster.S2C.sendToPlayer(new SyncBlockEntityTrackedDataS2C(blockEntity.getBlockPos(), trackedDataKey, writeToNetwork()), player);
    }
}
