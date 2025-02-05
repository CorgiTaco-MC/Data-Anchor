package com.example.examplemod.data.chunk;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class ChunkTrackedData implements TrackedData<ChunkAccess> {

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
}
