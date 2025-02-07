package com.example.examplemod.data.type.blockentity;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract sealed class BlockEntityTrackedData implements TrackedData<BlockEntity> permits ServerBlockEntityTrackedData, SyncedBlockEntityTrackedData {


    protected final TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey;
    protected final BlockEntity blockEntity;

    public BlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
        this.trackedDataKey = trackedDataKey;
        this.blockEntity = blockEntity;
    }

    @Override
    public BlockEntity get() {
        return this.blockEntity;
    }
}
