package com.example.examplemod.data.type.chunk;

import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract sealed class LevelChunkTrackedData extends ChunkTrackedData permits ServerLevelChunkTrackedData, SyncedLevelChunkTrackedData {
    public LevelChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, LevelChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public LevelChunk get() {
        return (LevelChunk) super.get();
    }
}
