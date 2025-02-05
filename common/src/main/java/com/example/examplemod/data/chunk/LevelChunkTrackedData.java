package com.example.examplemod.data.chunk;

import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract class LevelChunkTrackedData extends ChunkTrackedData {
    public LevelChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, LevelChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public LevelChunk get() {
        return (LevelChunk) super.get();
    }
}
