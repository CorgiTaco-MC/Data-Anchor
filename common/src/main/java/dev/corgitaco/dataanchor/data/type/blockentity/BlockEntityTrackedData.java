package dev.corgitaco.dataanchor.data.type.blockentity;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract sealed class BlockEntityTrackedData implements TrackedData<BlockEntity>, DirtyMarker permits ServerBlockEntityTrackedData, SyncedBlockEntityTrackedData {


    protected transient final TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey;
    protected transient final BlockEntity blockEntity;

    public BlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
        this.trackedDataKey = trackedDataKey;
        this.blockEntity = blockEntity;
    }

    @Override
    public BlockEntity get() {
        return this.blockEntity;
    }

    @Override
    public void markDirty() {
        blockEntity.setChanged();
    }

    @Override
    public void clearDirty() {

    }
}
