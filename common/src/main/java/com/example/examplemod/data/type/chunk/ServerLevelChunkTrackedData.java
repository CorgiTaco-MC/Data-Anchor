package com.example.examplemod.data.type.chunk;

import com.example.examplemod.data.ServerTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract non-sealed class ServerLevelChunkTrackedData extends LevelChunkTrackedData implements ServerTrackedData {

    public ServerLevelChunkTrackedData(TrackedDataKey<? extends LevelChunkTrackedData> trackedDataKey, LevelChunk levelChunk) {
        super(trackedDataKey, levelChunk);
    }
}
