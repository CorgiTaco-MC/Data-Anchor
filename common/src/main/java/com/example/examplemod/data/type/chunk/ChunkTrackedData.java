package com.example.examplemod.data.type.chunk;

import com.example.examplemod.data.DirtyMarker;
import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract sealed class ChunkTrackedData implements TrackedData<ChunkAccess>, DirtyMarker permits LevelChunkTrackedData, ProtoChunkTrackedData {

    protected final TrackedDataKey<? extends ChunkTrackedData> trackedDataKey;
    protected final ChunkAccess chunk;

    public ChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ChunkAccess chunk) {
        this.trackedDataKey = trackedDataKey;
        this.chunk = chunk;
    }

    @Override
    public ChunkAccess get() {
        return this.chunk;
    }

    @Override
    public void markDirty() {
        chunk.setUnsaved(true);
    }

    @Override
    public void clearDirty() {

    }
}
