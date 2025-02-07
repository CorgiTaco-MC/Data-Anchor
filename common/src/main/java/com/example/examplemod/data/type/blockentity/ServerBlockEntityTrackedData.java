package com.example.examplemod.data.type.blockentity;

import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract non-sealed class ServerBlockEntityTrackedData extends BlockEntityTrackedData {
    public ServerBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
        super(trackedDataKey, blockEntity);
    }
}
